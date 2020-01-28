package org.jccode.webcrawler.util;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jccode.webcrawler.model.ProxyModel;

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

    public static boolean proxyReliable(ProxyModel proxy) {
        return proxyReliable(proxy.getHost(), proxy.getPort());
    }

    public static boolean proxyReliable(String host, int port) {
        CloseableHttpClient client = HttpClientBuilder.create()
                .setProxy(new HttpHost(host, port))
                .build();
        HttpGet get = new HttpGet(DEFAULT_REQUEST_URL);
        try (CloseableHttpResponse response = client.execute(get)) {
            int code = response.getStatusLine().getStatusCode();
            return code >= 200 && code < 300;
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

    public static boolean isOk(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 200 && code < 300;
    }

    public static boolean isRedirect(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 300 && code < 400;
    }

    public static boolean isError(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 400;
    }

    public static void printHeaders(HttpResponse response){
        System.out.println("*****************************");
        for(Header header:response.getAllHeaders()){
            System.out.println(header.toString());
        }
        System.out.println("*****************************");
    }
}
