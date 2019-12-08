package org.jccode.webcrawler.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.Task;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MultiTasksHttpDownloader
 * <p>
 * 1、这里同时整合了抓取和下载网页的功能，需要进行拆分;
 * 2、需要提供自动切换代理功能
 * 3、需要提供自动添加和删除Header的功能
 * 4、使用线程池创建线程、处理如何关闭线程
 * <p>
 * 工作流程：发送请求 -> 接受请求 -> 填充Header字段 -> 循环往复
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/5 15:49
 * @Version 1.0
 **/
public class MultiTasksHttpDownloader {

    private String storagePath = "F://crawler//temp//";

    private final Logger log = Logger.getLogger(MultiTasksHttpDownloader.class);

    private CloseableHttpClient httpClient;

//    private HttpClientContext clientContext;

    private int threads;

    private HttpHost proxy;

    private List<Task> tasksList;


    public MultiTasksHttpDownloader(String storagePath, HttpHost proxy, Task... tasks) {
        this(proxy, tasks);
        this.storagePath = storagePath;
    }

    public MultiTasksHttpDownloader(HttpHost proxy, Task... tasks) {
        this.threads = tasks.length;
        this.proxy = proxy;
        this.tasksList = new ArrayList<>(Arrays.asList(tasks));
        initHttpClient();
    }


    public void download() throws InterruptedException {
        HttpClientThread[] threads = new HttpClientThread[getThreads()];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new HttpClientThread(httpClient, tasksList.get(i));
            threads[i].setName("HttpDownloaderThread-" + i);
        }

        for (HttpClientThread thread : threads) {
            thread.start();
        }
        for (HttpClientThread thread : threads) {
            thread.join();
        }

    }

//    public void setThread(int threads) {
//        if (threads <= 0)
//            throw new IllegalArgumentException("threads must be positive");
//        this.threads = threads > tasksList.size() ? threads : tasksList.size();
//    }
//
//    public void setProxy(HttpHost proxy) {
//        this.proxy = proxy;
//    }


    private void initHttpClient() {
        HttpClientBuilder builder = HttpClients.custom();

        SSLContext sslContext = SSLContexts.createSystemDefault();

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(10 * 1000)
                .build();

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext))
                        .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setCharset(StandardCharsets.UTF_8)
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .build();

        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        manager.setMaxTotal(threads);
        manager.setDefaultSocketConfig(socketConfig);
        manager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setRedirectsEnabled(true)
                .build();

        BasicCookieStore cookieStore = new BasicCookieStore();
        this.httpClient = builder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setUserAgent(HttpConstant.Header.USER_AGENT)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(proxy == null ? null :
                        new DefaultProxyRoutePlanner(proxy))
                .setConnectionManager(manager)
                .build();
    }


    private BasicHeader[] defaultHeaders() {
        BasicHeader[] headers = new BasicHeader[]{
                new BasicHeader("Accept-Encoding", "gzip, deflate"),
                new BasicHeader("Accept", "text/html,application/xhtml+xml," +
                        "application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8," +
                        "application/signed-exchange;v=b3"),
                new BasicHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"),
                new BasicHeader("DNT", "1"),
                new BasicHeader("Connection", "Keep-Alive"),
        };
        return headers;
    }

    class HttpClientThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpClientContext clientContext;
        private final Task task;

        public HttpClientThread(CloseableHttpClient httpClient, Task task) {
            this.httpClient = httpClient;
            this.clientContext = HttpClientContext.create();
            this.task = task;
        }

        @Override
        public void run() {
            HttpUriRequest request = task.getRequest();
            request.setHeaders(defaultHeaders());
            try (CloseableHttpResponse response = httpClient.execute(request,
                    clientContext)) {
                log.info(response.getStatusLine());
                String filePath = storagePath + request.getURI().getHost() + ".txt";

                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
                char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, len);
                }
                writer.flush();

                stream.close();
                EntityUtils.consumeQuietly(entity);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public List<Task> getTasksList() {
        return tasksList;
    }

    public void setTasksList(List<Task> tasksList) {
        this.tasksList = tasksList;
    }
}
