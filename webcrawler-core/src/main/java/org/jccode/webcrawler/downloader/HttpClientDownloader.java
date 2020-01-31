package org.jccode.webcrawler.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.*;
import org.jccode.webcrawler.system.SystemProxyStrategy;
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
 * <p>
 * 包装器模式：包含两个HttpClientDownloader，一个使用代理，一个不使用
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
        CloseableHttpResponse response;
        CloseableHttpClient httpClient = getHttpClient(configuration);
        HttpClientRequestContext requestContext = requestConverter.convert(task,
                configuration, proxy);
        WebPage webPage = WebPage.fail();
        try {
            response = httpClient.execute(requestContext.getRequest(),
                    requestContext.getContext());
            webPage = handleResponse(response, task);
            logger.info("Download Page success: {}", webPage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Download Page failed: {},{}", webPage.getPath(), e.getMessage());
            return webPage;
        }
        return webPage;
    }


    private CloseableHttpClient getHttpClient(HttpClientConfiguration configuration) {
        String host = configuration.getHost();
        CloseableHttpClient client = httpClients.get(host);
        if (client == null) {
            client = downloaderBuilder.build(configuration);
            httpClients.putIfAbsent(host, client);
        }
        return client;
    }

    private WebPage handleResponse(CloseableHttpResponse response, Task task) {
        HttpEntity entity = response.getEntity();
        WebPage webPage = new WebPage();
        try {
            if (entity.getContentType().getValue().contains(HttpConstant.ContentType.TEXT_HTML)) {
                String rawText = EntityUtils.toString(entity);
                webPage.setBinary(false);
                webPage.setRawText(rawText);
                webPage.setTitle(HttpUtils.getTitle(rawText));
                webPage.setContent(HttpUtils.getContent(rawText));
                webPage.setContentType(HttpUtils.getContentType(entity.getContentType().getValue()));
                webPage.setCharSet(HttpUtils.getCharset(rawText));
            } else {
                webPage.setBinary(true);
                webPage.setContentType(HttpConstant.ContentType.MULTIPART);
                webPage.setBytes(IOUtils.toByteArray(entity.getContent()));
            }
            webPage.setContentLength(entity.getContentLength());
            webPage.setSite(task.getHost());
            webPage.setPath(task.getUrl());
            webPage.setStatus(response.getStatusLine().getStatusCode());
            webPage.setHeaders(HttpUtils.convertHeaders(response.getAllHeaders()));
            webPage.setTime(LocalDateTime.now());
            webPage.setDownloadSuccess(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webPage;
    }



}
