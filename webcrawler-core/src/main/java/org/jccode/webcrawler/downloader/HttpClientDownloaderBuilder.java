package org.jccode.webcrawler.downloader;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
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
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.jccode.webcrawler.conts.HttpClientConstant;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.HttpClientConfiguration;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HttpClientDownloaderBuilder
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/6 21:51
 * @Version 1.0
 **/
public class HttpClientDownloaderBuilder {

    private HttpHost proxy;

    private int maxTotal = 100;

    private PoolingHttpClientConnectionManager connectionManager;

    private static volatile HttpClientDownloaderBuilder INSTANCE;

    private HttpClientDownloaderBuilder() {
        initConnectionManager();
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


    public HttpClientDownloaderBuilder setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpClientDownloaderBuilder setMaxTotal(int total) {
        this.maxTotal = total;
        return this;
    }

    @Deprecated
    public InternalHttpClientDownloader build() {
        return new InternalHttpClientDownloader(defaultClient());
    }

    private CloseableHttpClient defaultClient() {
        HttpClientBuilder builder = HttpClients.custom();

        connectionManager.setMaxTotal(500);
        connectionManager.setDefaultMaxPerRoute(connectionManager.getMaxTotal() / 10);

        // RequestConfig 在HttpRequestConverter中为每一个请求单独设置


        return builder
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setRedirectStrategy(new DefaultRedirectStrategy())
                // 每个Request都单独设置了UA
                .setUserAgent(HttpConstant.Header.USER_AGENT)
                //每个Request都设置了单独的CookieStore
//                .setDefaultCookieStore(new BasicCookieStore())
//                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(proxy == null ? null :
                        new DefaultProxyRoutePlanner(proxy))
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy())
//                .useSystemProperties()// 如果设置了jvm的代理参数，设置此项可以强制使用代理
                .build();
    }

    /******************************************************************************/

    public CloseableHttpClient build(HttpClientConfiguration configuration) {
        return customClient(configuration);
    }

    private CloseableHttpClient customClient(HttpClientConfiguration configuration) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        connectionManager.setMaxTotal(maxTotal);
        httpClientBuilder.setConnectionManager(connectionManager);
        String var1;
        if ((var1 = configuration.getUserAgent()) != null) {
            httpClientBuilder.setUserAgent(var1);
        }
        if (configuration.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) (httpRequest
                    , httpContext) -> {
                if (!httpRequest.containsHeader("Accept-Encoding")) {
                    httpRequest.addHeader("Accept-Encoding", "gzip");
                }
            });
        }
        httpClientBuilder.setRedirectStrategy(new DefaultRedirectStrategy());
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(configuration.getTimeout())
                .build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(configuration.getRetryTimes(), true));
        httpClientBuilder.setDefaultCookieStore(generateCookies(configuration));
//        httpClientBuilder.setRoutePlanner(proxy == null ? null :
//                new DefaultProxyRoutePlanner(proxy));
        return httpClientBuilder.build();
    }

    private CookieStore generateCookies(HttpClientConfiguration configuration) {
        CookieStore cookieStore = new BasicCookieStore();
        if (!configuration.isDisableCookieManagement() && configuration.getDefaultCookies().size() != 0) {
            for (Map.Entry<String, String> entry :
                    configuration.getDefaultCookies().entrySet()) {
                cookieStore.addCookie(new BasicClientCookie(entry.getKey(),
                        entry.getValue()));
            }
        }
        return cookieStore;
    }

    private void initConnectionManager() {
        SocketConfig socketConfig = SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoTimeout(HttpClientConstant.Time.DEFAULT_TIMEOUT)
                .build();
        SSLContext sslContext = SSLContexts.createSystemDefault();
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

        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultConnectionConfig(connectionConfig);
    }


    private ConnectionKeepAliveStrategy keepAliveStrategy() {
        return (httpResponse, httpContext) -> {
            HeaderElementIterator iterator =
                    new BasicHeaderElementIterator(httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (iterator.hasNext()) {
                HeaderElement e = iterator.nextElement();
                String param = e.getName();
                String value = e.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return HttpClientConstant.Time.DEFAULT_KEEP_ALIVE;
        };
    }

}
