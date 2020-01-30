package org.jccode.webcrawler.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.*;
import org.jccode.webcrawler.system.SystemProxyStrategy;
import org.jccode.webcrawler.system.SystemProxyStrategyRegister;
import org.jccode.webcrawler.util.HttpUtils;
import org.jccode.webcrawler.util.IOUtils;
import org.jccode.webcrawler.util.ProxyUtil;

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

    private static final Logger logger = Logger.getLogger(HttpClientDownloader.class);

    private final InternalHttpClientDownloader proxyClient;
    private final InternalHttpClientDownloader directClient;

    private final SystemProxyStrategyRegister register =
            SystemProxyStrategyRegister.getInstance();

    public HttpClientDownloader() {
        HttpClientDownloaderBuilder builder = HttpClientDownloaderBuilder.create();
        directClient = builder.build();
        SystemProxyStrategy strategy = register.system();
        // 判断系统代理是否可用
        if (strategy != null) {
            ProxyModel proxyModel = strategy.inspect();
            HttpHost proxy = new HttpHost(proxyModel.getHost(), proxyModel.getPort());
            proxyClient = builder.setProxy(proxy).build();
        } else {
            proxyClient = null;
        }
    }


    public List<WebPage> download(Task[] tasks) {
        List<Task> directRequests = new ArrayList<>();
        List<Task> proxyRequests = new ArrayList<>();
        List<WebPage> webPageList = new ArrayList<>(tasks.length);
        int failCount = 0;
        for (Task task : tasks) {
            if (task.isUseProxy()) {
                if (proxyClient != null) {
                    proxyRequests.add(task);
                } else {
                    failCount++;
                }
            } else {
                directRequests.add(task);
            }
        }
        if (proxyClient != null && !proxyRequests.isEmpty()) {
            webPageList.addAll(proxyClient.download(proxyRequests));
        } else {
            logger.warn("[ " + failCount + " tasks failed : proxy unavailable ]");
        }
        webPageList.addAll(directClient.download(directRequests));
        webPageList.sort(((o1, o2) -> o2.getTime().compareTo(o1.getTime())));
        return webPageList;
    }

    public void close() {
        if (proxyClient != null) {
            proxyClient.close();
        }
        if (directClient != null) {
            directClient.close();
        }
    }

    /********************************************************************/

    private final Map<String, CloseableHttpClient> httpClients =
            new ConcurrentHashMap<>();

    private ProxyModel proxy;

    private HttpClientDownloaderBuilder downloaderBuilder =
            HttpClientDownloaderBuilder.create();
    private HttpRequestConverter requestConverter = new HttpRequestConverter();


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
            logger.info("Download Page success: " + webPage.getSite() + webPage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Download Page failed: " + webPage.getSite() + webPage.getPath(), e);
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
            if (entity.getContentType().getValue().contains(HttpConstant.ContentType.TEXT)) {
                String rawText = EntityUtils.toString(entity);
                webPage.setBinary(false);
                webPage.setRawText(rawText);
                webPage.setTitle(HttpUtils.getTitle(rawText));
                webPage.setContent(HttpUtils.getContent(rawText));
                webPage.setContentType(entity.getContentType().getValue());
            } else {
                webPage.setBinary(true);
                webPage.setContentType(HttpConstant.ContentType.MULTIPART);
                webPage.setBytes(IOUtils.toByteArray(entity.getContent()));
            }
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

    public HttpClientDownloader setProxy(ProxyModel proxy) {
        if (proxy != null || ProxyUtil.validateProxy(proxy.getHost(), proxy.getPort())) {
            this.proxy = proxy;
        } else {  // 检查系统代理是否可用
            ProxyModel systemProxy = register.system().inspect();
            if (ProxyUtil.validateProxy(systemProxy.getHost(), systemProxy.getPort())) {
                this.proxy = systemProxy;
            }
        }
        return this;
    }

}
