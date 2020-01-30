package org.jccode.webcrawler.downloader;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

/**
 * HttpClientRequestContext
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 12:25
 * @Version 1.0
 **/
public class HttpClientRequestContext {

    private HttpUriRequest request;
    private HttpClientContext context;

    public HttpUriRequest getRequest() {
        return request;
    }

    public void setRequest(HttpUriRequest request) {
        this.request = request;
    }

    public HttpClientContext getContext() {
        return context;
    }

    public void setContext(HttpClientContext context) {
        this.context = context;
    }
}
