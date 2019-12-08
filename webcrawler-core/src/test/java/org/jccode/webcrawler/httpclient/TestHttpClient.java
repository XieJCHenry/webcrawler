package org.jccode.webcrawler.httpclient;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.conts.HttpConstant;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.ProxySelector;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TestHttpClient
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/3 13:15
 * @Version 1.0
 **/
public class TestHttpClient {

    private static Logger log = Logger.getLogger(TestHttpClient.class);


    /**
     * 具体参见官网示例：https://hc.apache.org/httpcomponents-client-4.5
     * .x/httpclient/examples/org/apache/http/examples/client/ClientConfiguration.java
     */
    @Test
    public void createDefaultHttpClient() {
        // 为安全连接创建一个SSL上下文
        SSLContext sslContext = SSLContexts.createSystemDefault();
        // 为支持的协议方案创建自定义的Socket工厂注册表
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext))
                        .build();

        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(10)
                .build();
        // 为所有Socket连接设定参数
        manager.setDefaultSocketConfig(socketConfig);
        // 为特定Socket端口设定参数
        manager.setSocketConfig(new HttpHost("", 80), socketConfig);
        manager.setValidateAfterInactivity(1000);
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .build();
        // 为所有端口设置默认连接参数
        manager.setDefaultConnectionConfig(connectionConfig);
        // 为特定端口设置连接参数
        manager.setConnectionConfig(new HttpHost("", 80), connectionConfig);

        CookieStore cookieStore = new BasicCookieStore();

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        // 设置代理
        HttpHost proxy = new HttpHost("", 8888);
        DefaultProxyRoutePlanner proxyRoutePlanner = new DefaultProxyRoutePlanner(proxy);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(manager)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setRoutePlanner(proxyRoutePlanner)
                .build();

        // 上述配置在HttpRequest层次基本可以被重新覆盖
    }


    /**
     * 围绕HttpClient的一些配置
     */
    @Test
    public void httpClientSpec1() {
        String host = "";
        long keep = 3000;

        // 1、请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)    // 设置Cookie策略
//                .setProxy(new HttpHost("127.0.0.1", 1080))
                .build();

        /*===================================================================*/

        // 2、手动添加一个CookieStore，避免默认的CookieStore被gc掉
        CookieStore cookieStore = new BasicCookieStore();
        Cookie cookie = new BasicClientCookie("", "");
        cookieStore.addCookie(cookie);

        /*===================================================================*/

        // 3、设置连接管理器（PoolingHttpClientConnectionManager支持多线程执行请求）
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        // 3.1 设置最大连接数为200
        connectionManager.setMaxTotal(200);
        // 3.2 设置每个路由最大连接数为20
        connectionManager.setDefaultMaxPerRoute(20);
        // 3.3 设置localhost:80的最大连接数为50
        HttpHost localhost = new HttpHost("127.0.0.1", 80);
        connectionManager.setMaxPerRoute(new HttpRoute(localhost), 50);

        /*===================================================================*/

        // 4、设置代理
        // 4.1 方式一
        HttpHost proxy = new HttpHost("127.0.0.1", 1080);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        // 4.2 方式二
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1080");
        SystemDefaultRoutePlanner defaultRoutePlanner = new SystemDefaultRoutePlanner(
                ProxySelector.getDefault());

        /*===================================================================*/

        // 每个httpClient在执行请求的过程中，自动将Lookup、CookieSpe、CookieOrigin、CookieStore
        // 写入到自己的HttpClientContext，或从自身的HttpClientContext中读取。
        // 可以为每一个HttpClient创建独立的HttpClientContext，当需要多线程使用的时候。
        HttpClientContext context = HttpClientContext.create();


        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setKeepAliveStrategy(new BasicConnectionKeepAliveStrategy(host, keep))
                .setConnectionManager(connectionManager)
                .setRoutePlanner(routePlanner)  // defaultRoutePlanner
                .build();
    }

    // 设置连接保持策略
    static class BasicConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
        private String host;
        private long timeToKeepAlive;    // 单位是毫秒

        BasicConnectionKeepAliveStrategy(String host, long timeToKeepAlive) {
            this.host = host;
            this.timeToKeepAlive = timeToKeepAlive;
        }

        @Override
        public long getKeepAliveDuration(HttpResponse httpResponse,
                                         HttpContext httpContext) {
            HeaderElementIterator iterator = new BasicHeaderElementIterator(
                    httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (iterator.hasNext()) {
                HeaderElement element = iterator.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            HttpHost targetHost =
                    (HttpHost) httpContext.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
            if (targetHost != null && host.equalsIgnoreCase(targetHost.getHostName())) {
                return timeToKeepAlive;
            }
            return 0;
        }
    }

    /*====================================================================*/

    /**
     * 连接驱逐策略，需要额外开启线程运行
     * <p>
     * 驱逐空闲或已被服务器关闭的连接
     */
    static class IdleConnectionMonitorThread extends Thread {

        private final int timeout;
        private final HttpClientConnectionManager manager;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(int timeout,
                                           HttpClientConnectionManager manager) {
            super();
            this.timeout = timeout;
            this.manager = manager;
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    synchronized (this) {
                        wait(5000);
                        // 关闭失效连接
                        manager.closeExpiredConnections();
                        // 关闭已空闲 timeout 秒的连接
                        manager.closeIdleConnections(timeout, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException ignore) {
                }
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();    // 通知所有线程
            }
        }
    }


    /*====================================================================*/

    /**
     * 无法解决: post/redirect/post过程中请求体丢失，需要自定义RedirectStrategy。
     */
    @Test
    public void testDefaultRedirectStrategy() {
        String url = "https://www.360buy.com";
        CookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        CloseableHttpClient client = HttpClients.custom()
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .setUserAgent(HttpConstant.USER_AGENT)
                .build();

        HttpGet get = new HttpGet(url);

        try (CloseableHttpResponse response = client.execute(get)) {
            printHeaders(response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 应该用另一种方式实现多线程，需要保证Socket连接时间足够长
     *
     * @throws InterruptedException
     */
    @Test
    public void testMultiThreadDownload() throws InterruptedException {
        PoolingHttpClientConnectionManager manager =
                new PoolingHttpClientConnectionManager();
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(manager)
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultCookieStore(new BasicCookieStore())
                .setUserAgent(HttpConstant.USER_AGENT)
                .setDefaultHeaders(Arrays.asList(defaultHeaders()))
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .build();
        String[] urisToGet = {
                "https://cn.bing.com/",
                "https://www.360buy.com/",
                "https://juejin.im/",
        };
        String folder = "F:\\crawler\\temp\\";
        HttpClientThread[] threads = new HttpClientThread[urisToGet.length];
        for (int i = 0; i < threads.length; i++) {
            HttpGet get = new HttpGet(urisToGet[i]);
            threads[i] = new HttpClientThread(client, get, folder);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        new IdleConnectionMonitorThread(10000, manager).start();
    }

    /**
     * 将下载的网页写入到文件中，必须保证文件夹已存在
     */
    static class HttpClientThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpGet;
        private final String path;

        public HttpClientThread(CloseableHttpClient httpClient,
                                HttpGet httpGet, String path) {
            this.httpClient = httpClient;
            this.context = HttpClientContext.create();
            this.httpGet = httpGet;
            this.path = path;
        }

        @Override
        public void run() {
            try (CloseableHttpResponse response = httpClient.execute(httpGet, context)) {
                String filePath = path + httpGet.getURI().getHost() + ".txt";
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                }
                writer.flush();

                EntityUtils.consumeQuietly(entity);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 本示例演示如何在正常完成之前中止HTTP方法
     */
    @Test
    public void testAbort() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://www.baidu.com/");
        try (CloseableHttpResponse response = client.execute(get)) {
            printHeaders(response.getAllHeaders());
            get.abort();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用本地HttpClientContext保存同一个HttpClient中多个请求间的Cookie，或者其他信息
     */
    @Test
    public void testUseHttpClientContext() {
        CloseableHttpClient client = HttpClients.createDefault();
        CookieStore cookieStore = new BasicCookieStore();

        HttpClientContext clientContext = new HttpClientContext();
        clientContext.setCookieStore(cookieStore);
        HttpGet get = new HttpGet("https://www.baidu.com");

        try (CloseableHttpResponse response = client.execute(get, clientContext)) {
            System.out.println("-------------------------------------------");
            System.out.println(response.getStatusLine());
//            printHeaders(response.getAllHeaders());
            List<Cookie> cookies = cookieStore.getCookies();
            cookies.forEach(System.out::println);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateCustomSSLConnection() throws KeyManagementException,
            NoSuchAlgorithmException {
        SSLContext sslContext = SSLContexts.custom().build();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());// 绕过安全证书
        CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(factory)
                .build();

        // 下面正常执行请求
    }


    /*====================================================================*/


    private BasicHeader[] defaultHeaders() {
        BasicHeader[] headers = new BasicHeader[]{
//                new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; " +
//                        "x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904" +
//                        ".108 Safari/537.36"),
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

    private void printEntityInfo(HttpEntity entity) {
        System.out.println("*********************************************");
        System.out.println("ContentType = " + entity.getContentType());
        System.out.println("ContentLength = " + entity.getContentLength());
        System.out.println("ContentEncoding = " + entity.getContentEncoding());
        System.out.println("*********************************************");
    }

    private void printHeaders(Header[] headers) {
        System.out.println("*********************************************");
        for (Header header : headers) {
            System.out.println(header.toString());
        }
        System.out.println("*********************************************");
    }

}
