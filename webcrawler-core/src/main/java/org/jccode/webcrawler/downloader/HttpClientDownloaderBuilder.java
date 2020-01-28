package org.jccode.webcrawler.downloader;

import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.jccode.webcrawler.conts.HttpConstant;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpClientDownloaderBuilder
 * <p>
 * builder创建client实例，每个实例都可以进行多线程下载。
 * 最初目的是为了整合三个搜索引擎的结果，考虑到后期可能扩展到可以自定义数据来源网站，因此
 * 采用的方案是：“一个数据源网站对应一个client”。对于每一次搜索
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/6 21:51
 * @Version 1.0
 **/
public class HttpClientDownloaderBuilder {

    private static final int DEFAULT_THREAD_NUM =
            Runtime.getRuntime().availableProcessors() + 1;

    private static final int DEFAULT_MAX_THREAD_NUM = DEFAULT_THREAD_NUM * 2;

    private static final long DEFAULT_KEEP_ALIVE = 60 * 1000;

    private static final int DEFAULT_TIMEOUT = 5000;

    private static final String DEFAULT_THREAD_NAME = "InternalHttpClientDownloader" +
            "-Thread";

    private Integer threads;

    private Integer timeOut;

    private ExecutorService executorService;

    private AtomicInteger threadAlive = new AtomicInteger(0);

    private String threadName;

//    private CloseableHttpClient httpClient;

    private HttpHost proxy;

    private static volatile HttpClientDownloaderBuilder INSTANCE;

    private HttpClientDownloaderBuilder() {
    }

    public static HttpClientDownloaderBuilder create() {
        if (INSTANCE == null) {
            synchronized (HttpClientDownloaderBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpClientDownloaderBuilder();
                }
            }
        }
        return INSTANCE;
    }


    public HttpClientDownloaderBuilder setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public HttpClientDownloaderBuilder setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    public HttpClientDownloaderBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public HttpClientDownloaderBuilder setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }


    public HttpClientDownloaderBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public InternalHttpClientDownloader build() {
        return new InternalHttpClientDownloader(generateClient());
    }

    private CloseableHttpClient generateClient() {
        HttpClientBuilder builder = HttpClients.custom();

        SSLContext sslContext = SSLContexts.createSystemDefault();

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(timeOut != null ? timeOut : DEFAULT_TIMEOUT)
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
        manager.setMaxTotal(500);
        manager.setDefaultMaxPerRoute(manager.getMaxTotal() / 10);
        manager.setDefaultSocketConfig(socketConfig);
        manager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setRedirectsEnabled(true)
                .build();


        ConnectionKeepAliveStrategy keepAliveStrategy = (httpResponse, httpContext) -> {
            HeaderElementIterator iter = new BasicHeaderElementIterator(
                    httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (iter.hasNext()) {
                HeaderElement e = iter.nextElement();
                String param = e.getName();
                String value = e.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE;
        };

        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient client = builder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setUserAgent(HttpConstant.Header.USER_AGENT)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(proxy == null ? null :
                        new DefaultProxyRoutePlanner(proxy))
                .setConnectionManager(manager)
                .setKeepAliveStrategy(keepAliveStrategy)
//                .useSystemProperties()// 如果设置了jvm的代理参数，设置此项可以强制使用代理
                .build();
        return client;
    }

}
