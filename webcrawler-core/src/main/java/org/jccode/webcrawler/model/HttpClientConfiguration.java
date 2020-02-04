package org.jccode.webcrawler.model;

import org.jccode.webcrawler.conts.HttpClientConstant;
import org.jccode.webcrawler.conts.HttpConstant;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientConfiguration
 * <p>
 * HttpClient的配置
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 14:52
 * @Version 1.0
 **/
public class HttpClientConfiguration {

    private String userAgent;

    private String host;

    private String charset;

    private int timeout;

    private int retryTimes;

    private int sleepTime;

    private boolean useGzip;

    private boolean disableCookieManagement;

    private Map<String, String> defaultCookies = new HashMap<>();

    private Map<String, String> defaultHeaders = new HashMap<>();

    private HttpClientConfiguration() {
        this.userAgent = HttpConstant.Header.USER_AGENT;
        this.charset = Charset.forName("utf-8").name();
        this.timeout = HttpClientConstant.Time.DEFAULT_TIMEOUT;
        this.retryTimes = HttpClientConstant.Time.DEFAULT_RETRY_TIMES;
        this.sleepTime = HttpClientConstant.Time.DEFAULT_SLEEP_TIMES;
        this.useGzip = false;
        this.disableCookieManagement = false;
    }

    public static HttpClientConfiguration custom() {
        return new HttpClientConfiguration();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getHost() {
        return host;
    }

    public String getCharset() {
        return charset;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public boolean isDisableCookieManagement() {
        return disableCookieManagement;
    }

    public Map<String, String> getDefaultCookies() {
        return defaultCookies;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public HttpClientConfiguration setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public HttpClientConfiguration setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpClientConfiguration setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public HttpClientConfiguration setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public HttpClientConfiguration setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public HttpClientConfiguration setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public HttpClientConfiguration setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }

    public HttpClientConfiguration setDisableCookieManagement(boolean disableCookieManagement) {
        this.disableCookieManagement = disableCookieManagement;
        return this;
    }

    public HttpClientConfiguration setDefaultCookies(Map<String, String> defaultCookies) {
        this.defaultCookies = defaultCookies;
        return this;
    }

    public HttpClientConfiguration setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
        return this;
    }
}
