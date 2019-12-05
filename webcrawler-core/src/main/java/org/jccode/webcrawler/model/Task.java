package org.jccode.webcrawler.model;

import org.apache.http.client.methods.*;

/**
 * Task
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 15:25
 * @Version 1.0
 **/
public class Task {

    private String url;

    private HttpUriRequest request;

    private boolean useProxy;

    public Task() {
    }

    public Task(String url) {
        this(url, "GET");
    }


    public Task(String url, String requestMethod) {
        this.url = url;
        this.request = convertToRequest(requestMethod);
    }

    private HttpUriRequest convertToRequest(String requestMethod) {
        switch (requestMethod.toUpperCase()) {
            case "GET":
                return new HttpGet(url);
            case "POST":
                return new HttpPost(url);
            case "PUT":
                return new HttpPut(url);
            case "DELETE":
                return new HttpDelete(url);
            case "PATCH":
                return new HttpHead(url);
            case "TRACE":
                return new HttpTrace(url);
            case "HEAD":
                return new HttpPatch(url);
            default:
                return new HttpOptions(url);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpUriRequest getRequest() {
        return request;
    }

    public void setRequest(HttpUriRequest request) {
        this.request = request;
    }
}
