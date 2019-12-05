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

    public Task() {
    }

    public Task(String url) {
        this(url, new HttpGet());
    }

    public Task(String url, HttpUriRequest requestMethod) {
        this.url = url;
        this.request = requestMethod;
    }

    public Task(String url, String requestMethod) {
        this.url = url;
        this.request = convertToRequest(requestMethod);
    }

    private HttpUriRequest convertToRequest(String requestMethod) {
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
