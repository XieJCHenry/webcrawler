package org.jccode.webcrawler.http;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * DefaultRequestRetryHandler
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/9 15:01
 * @Version 1.0
 **/
public class DefaultRequestRetryHandler implements HttpRequestRetryHandler {
    private final Logger log = Logger.getLogger(DefaultRequestRetryHandler.class);
    private final int DEFAULT_RETRY_TIMES = 3;

    @Override
    public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
        if (i > DEFAULT_RETRY_TIMES) {
            log.error("retry has more than 3 time,abandon request");
            return false;
        }
        if (e instanceof ConnectTimeoutException) {
            log.error("Connection Time out");
            return false;
        }
        if (e instanceof NoHttpResponseException) {
            log.error("receive no response from server, retrying...");
            return true;
        }
        if (e instanceof InterruptedIOException) {
            log.error("InterruptedIOException");
            return false;
        }
        if (e instanceof UnknownHostException) {
            log.error("server host unknown");
            return false;
        }

        if (e instanceof SSLException) {
            log.error("SSL hand shake exception");
            return false;
        }

        HttpClientContext context = HttpClientContext.adapt(httpContext);
        HttpRequest request = context.getRequest();
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            // 如果请求不是关闭连接
            return true;
        }
        return false;
    }
}
