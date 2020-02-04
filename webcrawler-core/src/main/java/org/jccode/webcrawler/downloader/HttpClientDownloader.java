package org.jccode.webcrawler.downloader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.*;
import org.jccode.webcrawler.system.SystemProxyStrategyRegister;
import org.jccode.webcrawler.util.HttpUtils;
import org.jccode.webcrawler.util.IOUtils;
import org.jccode.webcrawler.util.ProxyUtil;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HttpClientDownloader
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/9 15:29
 * @Version 1.0
 **/
public class HttpClientDownloader {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientDownloader.class);

    private final SystemProxyStrategyRegister register = SystemProxyStrategyRegister.getInstance();

    private final Map<String, CloseableHttpClient> httpClients = new ConcurrentHashMap<>();

    private ProxyModel proxy;

    private HttpClientDownloaderBuilder downloaderBuilder = HttpClientDownloaderBuilder.create();
    private HttpRequestConverter requestConverter = new HttpRequestConverter();

    /**
     * 如果传入为NULL，则不应该设立Proxy；如果传入Proxy不可用，再去检查系统代理是否可用
     *
     * @param proxy
     * @return
     */
    public HttpClientDownloader setProxy(ProxyModel proxy) {
        if (proxy == null) {
            // do nothing
        } else if (ProxyUtil.validateProxy(proxy.getHost(), proxy.getPort())) {
            this.proxy = proxy;
        } else {
            ProxyModel systemProxy = register.system().inspect();
            if (ProxyUtil.validateProxy(systemProxy.getHost(), systemProxy.getPort())) {
                this.proxy = systemProxy;
            }
        }
        return this;
    }

    public WebPage download(Task task, HttpClientConfiguration configuration) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient(configuration);
        // 在此处解决代理问题：为每一个请求单独设置代理
        HttpClientRequestContext requestContext = requestConverter.convert(task,
                configuration, proxy);
        WebPage webPage = WebPage.fail();
        try {
            response = httpClient.execute(requestContext.getRequest(), requestContext.getContext());
            webPage = handleResponse(response, task);
            logger.info("Download Page success: {}", webPage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Download Page failed: {},{}", webPage.getPath(), e.getMessage());
            return webPage;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return webPage;
    }

    public void close() {
        for (Map.Entry<String, CloseableHttpClient> entry : httpClients.entrySet()) {
            CloseableHttpClient client = entry.getValue();
            try {
                if (client != null)
                    client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * HttpClients存储了不同域名所对应的CloseableHttpClient
     *
     * @param configuration
     * @return
     */
    private CloseableHttpClient getHttpClient(HttpClientConfiguration configuration) {
        String host = configuration.getHost();
        CloseableHttpClient client = httpClients.get(host);
        if (client == null) {
            client = downloaderBuilder.build(configuration);
            httpClients.putIfAbsent(host, client);
        }
        return client;
    }

    /**
     * 具体处理响应
     *
     * @param response
     * @param task
     * @return
     */
    private WebPage handleResponse(CloseableHttpResponse response, Task task) {
        HttpEntity entity = response.getEntity();
        Header[] headers = response.getAllHeaders();
        WebPage webPage = new WebPage();
        try {
            String contentType = HttpUtils.getContentType(headers);
            if (contentType.contains("text")) {
                String rawText = EntityUtils.toString(entity);
                webPage.setBinary(false);
                webPage.setRawText(rawText);
                webPage.setTitle(HttpUtils.getTitle(rawText));
                webPage.setContent(HttpUtils.getContent(rawText));
                webPage.setCharSet(HttpUtils.getCharset(rawText));
            } else {
                webPage.setBinary(true);
                webPage.setTitle(HttpUtils.getTitle(task));
                webPage.setBytes(IOUtils.toByteArray(entity.getContent()));
            }
            webPage.setSite(task.getHost());
            webPage.setPath(task.getUrl());
            webPage.setContentType(contentType);
            webPage.setContentLength(entity.getContentLength());
            webPage.setStatus(response.getStatusLine().getStatusCode());
            webPage.setHeaders(HttpUtils.convertHeaders(headers));
            webPage.setTime(LocalDateTime.now());
            webPage.setDownloadSuccess(true);
        } catch (IOException e) {
            logger.warn("Exception occurred while download page : {} , [{}]",
                    task.getUrl(), e.getMessage());
            return WebPage.fail();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return webPage;
    }


}
