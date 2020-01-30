package org.jccode.webcrawler.downloader;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.model.ProxyModel;
import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.jccode.webcrawler.system.SystemProxyStrategy;
import org.jccode.webcrawler.system.SystemProxyStrategyRegister;

import java.util.*;

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

//    public List<WebPage> download(List<Task> tasks) {
//
//    }

    public void close() {
        if (proxyClient != null) {
            proxyClient.close();
        }
        if (directClient != null) {
            directClient.close();
        }
    }
}
