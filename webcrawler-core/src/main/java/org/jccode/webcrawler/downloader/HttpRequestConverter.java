package org.jccode.webcrawler.downloader;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.jccode.webcrawler.model.HttpClientConfiguration;
import org.jccode.webcrawler.model.ProxyModel;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.util.UrlUtils;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * HttpRequestConverter
 *
 * @Description 处理请求的是否使用代理
 * @Author jc-henry
 * @Date 2020/1/30 12:23
 * @Version 1.0
 **/
public class HttpRequestConverter {

    public HttpClientRequestContext convert(Task task,
                                            HttpClientConfiguration configuration,
                                            ProxyModel proxy) {
        HttpClientRequestContext context = new HttpClientRequestContext();
        context.setRequest(convertToHttpUriRequest(task, configuration, proxy));
        context.setContext(convertToHttpClientContext(task));
        return context;
    }

    private HttpClientContext convertToHttpClientContext(Task task) {
        HttpClientContext context = new HttpClientContext();
        if (task.getCookies().size() != 0) {
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> entry : task.getCookies().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(),
                        entry.getValue());
                cookie.setDomain(UrlUtils.extractHost(task.getUrl()));
                cookieStore.addCookie(cookie);
            }
            context.setCookieStore(cookieStore);
        }
        return context;
    }

    private HttpUriRequest convertToHttpUriRequest(Task task,
                                                   HttpClientConfiguration configuration, ProxyModel proxy) {
        RequestBuilder requestBuilder =
                selectRequestMethod(task.getMethod()).setUri(task.getUrl());
        if (task.getHeaders() != null) {
            for (Map.Entry<String, String> entry : task.getHeaders().entrySet()) {
                requestBuilder.addHeader(new BasicHeader(entry.getKey(),
                        entry.getValue()));
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        requestConfigBuilder
                .setSocketTimeout(configuration.getTimeout())
                .setConnectTimeout(configuration.getTimeout())
                .setCookieSpec(CookieSpecs.STANDARD)
                .setRedirectsEnabled(true)
                .setExpectContinueEnabled(true);
        if (proxy != null) {
            requestConfigBuilder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort()));
        }

        requestBuilder.setConfig(requestConfigBuilder.build());
        requestBuilder.setCharset(Charset.forName(configuration.getCharset()));
        HttpUriRequest request = requestBuilder.build();
        if (task.getHeaders().size() != 0) {
            for (Map.Entry<String, String> entry : task.getHeaders().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }

    private RequestBuilder selectRequestMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return RequestBuilder.get();
            case "POST":
                return RequestBuilder.post();
            case "PUT":
                return RequestBuilder.put();
            case "DELETE":
                return RequestBuilder.delete();
            case "PATCH":
                return RequestBuilder.patch();
            case "TRACE":
                return RequestBuilder.trace();
            case "HEAD":
                return RequestBuilder.head();
            default:
                return RequestBuilder.options();
        }
    }
}
