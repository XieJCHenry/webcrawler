package org.jccode.webcrawler.downloader;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
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
import org.apache.http.ssl.SSLContexts;
import org.jccode.webcrawler.conts.HttpConst;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpClientDownloaderBuilder
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/6 21:51
 * @Version 1.0
 **/
public class HttpClientDownloaderBuilder {

    private static final int DEFAULT_THREAD_NUM =
            Runtime.getRuntime().availableProcessors() + 1;

    private static final int DEFAULT_MAX_THREAD_NUM = DEFAULT_THREAD_NUM * 2;

    private static final long DEFAULT_KEEP_ALIVE_TIME = 30;

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    private static final String DEFAULT_THREAD_NAME = "HttpClientDownloader-Thread";

    private int threads;

    private int timeOut;

    private ExecutorService executorService;

    private AtomicInteger threadAlive = new AtomicInteger(0);

    private String threadName;

    private int threadNum;

    private CloseableHttpClient httpClient;

    private HttpHost proxy;

    public HttpClientDownloaderBuilder() {
    }


//    public HttpClientDownloader build() {
//
//    }

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

    public HttpClientDownloaderBuilder setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public HttpClientDownloaderBuilder setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public HttpClientDownloaderBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    private void initHttpClient() {
        HttpClientBuilder builder = HttpClients.custom();

        SSLContext sslContext = SSLContexts.createSystemDefault();

        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(timeOut)
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
                .setUserAgent(HttpConst.Header.USER_AGENT)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(proxy == null ? null :
                        new DefaultProxyRoutePlanner(proxy))
                .setConnectionManager(manager)
                .build();
    }

//    private boolean checkProxyReliable() {
//
//    }
}
