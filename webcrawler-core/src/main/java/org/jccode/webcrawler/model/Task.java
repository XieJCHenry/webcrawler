package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Task
 * <p>
 * 下载器任务
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 15:25
 * @Version 1.0
 **/
@Data
@ToString
@EqualsAndHashCode
public class Task implements Serializable {


    private String host;

    private String url;


    private String charset="utf-8";

    private HttpUriRequest request;

    private boolean useProxy;

    private boolean executeSuccess;

    /**
     *
     * 如果请求的是二进制数据，不使用编码进行解析
     */
    private boolean isBinary;

    /**
     * priority of a task, the bigger will be processed earlier.
     */
    private long priority;

    private Map<String,String> headers = new HashMap<>();

    private Map<String,String> cookies = new HashMap<>();


    public Task() {
    }

    public Task(String url) {
        this(url, "GET");
    }


    public Task(String url, String requestMethod) {
        this.url = url;
        this.request = convertToRequest(requestMethod);
        this.host = request.getURI().getHost();
    }

    public Task(String url, boolean useProxy) {
        this(url);
        this.useProxy = useProxy;
    }

    public Task(String url, String requestMethod, String charset) {
        this(url, requestMethod);
        this.charset = charset;
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


}
