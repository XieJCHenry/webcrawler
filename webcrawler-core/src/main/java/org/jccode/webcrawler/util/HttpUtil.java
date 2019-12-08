package org.jccode.webcrawler.util;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jccode.webcrawler.model.ProxyModel;
import sun.net.spi.DefaultProxySelector;

import java.io.IOException;

/**
 * HttpUtil
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/7 15:36
 * @Version 1.0
 **/
public class HttpUtil {

    private static final String DEFAULT_REQUEST_URL = "https://www.google.com/";

    private HttpUtil() {
    }

    public static boolean proxyIsReliable(ProxyModel proxy) {
        return proxyIsReliable(proxy.getHost(), proxy.getPort());
    }

    public static boolean proxyIsReliable(String host, int port) {
        CloseableHttpClient client = HttpClientBuilder.create()
                .setProxy(new HttpHost(host, port))
                .build();
        HttpGet get = new HttpGet(DEFAULT_REQUEST_URL);
        try (CloseableHttpResponse response = client.execute(get)) {
            int code = response.getStatusLine().getStatusCode();
            return code >= 200 & code < 300;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                get.abort();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
