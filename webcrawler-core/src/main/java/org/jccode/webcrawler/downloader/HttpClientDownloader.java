package org.jccode.webcrawler.downloader;

import org.apache.http.HttpHost;
import org.jccode.webcrawler.model.ProxyModel;
import org.jccode.webcrawler.system.SystemProxyStrategy;
import org.jccode.webcrawler.system.SystemProxyStrategyRegister;

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

    private InternalHttpClientDownloader proxyClient;
    private InternalHttpClientDownloader directClient;

    private final SystemProxyStrategyRegister register =
            SystemProxyStrategyRegister.getInstance();

    private HttpClientDownloader() {
        HttpClientDownloaderBuilder builder = HttpClientDownloaderBuilder.create();
        ProxyModel proxy = register.system().inspect();
    }

}
