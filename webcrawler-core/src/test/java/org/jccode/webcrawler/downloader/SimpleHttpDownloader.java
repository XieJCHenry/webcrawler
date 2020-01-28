package org.jccode.webcrawler.downloader;

import com.google.common.base.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * SimpleHttpDownloader
 * <p>
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/4 14:51
 * @Version 1.0
 **/
public class SimpleHttpDownloader extends AbstractDownloader {

    private Logger log = Logger.getLogger(SimpleHttpDownloader.class);

    private CloseableHttpClient httpClient;

    private HttpClientContext clientContext = new HttpClientContext();

    private HttpHost proxy;

    private String url;

    private String requestMethod;

    private int threads;

    public SimpleHttpDownloader(Task task) {
        this.url = task.getUrl();
        this.requestMethod = task.getRequest().getMethod();
    }


    public SimpleHttpDownloader(String url, String requestMethod) {
        checkUrlLegality(url);
        this.requestMethod = requestMethod;
    }

    public SimpleHttpDownloader setProxy(String host, int port) {
        this.proxy = new HttpHost(host, port);
        return this;
    }


    @Override
    public String download() {
        initComponent();
        StringBuilder appender = new StringBuilder();
        HttpUriRequest request = convertToRequest(url, requestMethod);
        request.setHeaders(defaultHeaders());

        try (CloseableHttpResponse response = httpClient.execute(request,
                clientContext)) {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                log.info(response.getStatusLine());
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                char[] buffer = new char[1024];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    appender.append(buffer, 0, len);
                }

                input.close();
                EntityUtils.consumeQuietly(entity);
            } else {
                log.error("Unexpected response code : " + status);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return appender.toString();
    }

    private void initComponent() {
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

//        HttpHost proxy = new HttpHost("127.0.0.1", 8001);
//        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);


        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setRedirectsEnabled(true)
                .build();

        BasicCookieStore cookieStore = new BasicCookieStore();
        httpClient = builder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setUserAgent(HttpConstant.Header.USER_AGENT)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(proxy == null ? null :
                        new DefaultProxyRoutePlanner(proxy))
                .setConnectionManager(manager)
                .build();

        clientContext.setCookieStore(cookieStore);
        clientContext.setRequestConfig(requestConfig);
    }

    private void checkUrlLegality(String url) {
        if (Strings.isNullOrEmpty(url)) {
            log.error("url is null");
            throw new NullPointerException();
        } else {
            this.url = url;
        }
    }

    private HttpUriRequest convertToRequest(String url, String requestMethod) {
        if (requestMethod.equalsIgnoreCase("GET"))
            return new HttpGet(url);
        else if (requestMethod.equalsIgnoreCase("POST"))
            return new HttpPost(url);
        else if (requestMethod.equalsIgnoreCase("PUT"))
            return new HttpPut(url);
        else if (requestMethod.equalsIgnoreCase("DELETE"))
            return new HttpDelete(url);
        else if (requestMethod.equalsIgnoreCase("HEAD"))
            return new HttpHead(url);
        else if (requestMethod.equalsIgnoreCase("TRACE"))
            return new HttpTrace(url);
        else if (requestMethod.equalsIgnoreCase("PATCH"))
            return new HttpPatch(url);
        else
            return new HttpOptions(url);
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
}
