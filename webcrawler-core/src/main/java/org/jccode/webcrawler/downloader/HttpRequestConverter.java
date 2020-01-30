package org.jccode.webcrawler.downloader;

import org.apache.http.client.methods.HttpUriRequest;
import org.jccode.webcrawler.model.ProxyModel;
import org.jccode.webcrawler.model.Task;

/**
 * HttpRequestConverter
 *
 * @Description 处理请求的是否使用代理
 * @Author jc-henry
 * @Date 2020/1/30 12:23
 * @Version 1.0
 **/
public class HttpRequestConverter {

    public HttpClientRequestContext convert(Task task, ProxyModel proxy) {
        HttpClientRequestContext context = new HttpClientRequestContext();

        return context;
    }

    private HttpUriRequest convertToHttpUriRequest(){

    }
}
