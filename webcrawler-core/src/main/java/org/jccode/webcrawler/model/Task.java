package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.*;
import org.jccode.webcrawler.conts.HttpClientConstant;
import org.jccode.webcrawler.util.UrlUtils;

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

    public static final int DEFAULT_RETRIES = 3;

    private String host;

    private String url;

    private String method;

    private String charset = "utf-8";

    @Deprecated
    private HttpUriRequest request;

    private boolean useProxy = false;

    private boolean executeSuccess;

    private int retryTimes = DEFAULT_RETRIES;

    /**
     * 如果请求的是二进制数据，不使用编码进行解析
     */
    private boolean isBinary;

    /**
     * priority of a task, the bigger will be processed earlier.
     */
    private int priority = 0;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> cookies = new HashMap<>();


    public Task() {
    }

    public Task(String url) {
        this(url, "GET");
    }


    public Task(String url, String requestMethod) {
        this(url, requestMethod, false);
        this.host = UrlUtils.extractHost(url);
    }

    public Task(String url, boolean useProxy) {
        this(url);
        this.useProxy = useProxy;
    }

    public Task(String url, String requestMethod, boolean useProxy) {
        this.url = url;
        this.method = requestMethod;
        this.useProxy = useProxy;
    }

    public Task(String url, String requestMethod, String charset) {
        this(url, requestMethod, false);
        this.charset = charset;
    }

    public boolean decrementRetryTimes() {
        if (retryTimes == 0)
            return false;
        else {
            retryTimes--;
            return true;
        }
    }

}
