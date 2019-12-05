package org.jccode.webcrawler;

import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.jccode.webcrawler.downloader.DownloaderInterface;
import org.jccode.webcrawler.downloader.SimpleHttpDownloader;
import org.junit.Test;

/**
 * TestSimpleHttpDownloader
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/4 15:45
 * @Version 1.0
 **/
public class TestSimpleHttpDownloader {


    @Test
    public void testDownloadBiliBili() {
        String url = "https://www.bilibili.com/";
        String requestMethod = "GET";
        DownloaderInterface downloader = new SimpleHttpDownloader(url, requestMethod);
        String result = downloader.download();
        System.out.println(result);
    }

    @Test
    public void testDownloadGoogle() {
        String url = "https://www.google.com";
        String requestMethod = "GET";
        String host = "127.0.0.1";
        int port = 1080;
        DownloaderInterface downloader = new SimpleHttpDownloader(url, requestMethod)
                .setProxy(host, port);
        System.out.println(downloader.download());
    }

    @Test
    public void testDownloadWallHaven() {
        String url = "https://wallhaven.cc/toplist";
        String requestMethod = "GET";
        DownloaderInterface downloader = new SimpleHttpDownloader(url, requestMethod);
        System.out.println(downloader.download());
    }

    @Test
    public void testDownloadGitHubSpring() {
        String url = "https://github.com/search?o=desc&q=spring&s=stars&type" +
                "=Repositories";
        String method = "GET";
        DownloaderInterface downloader = new SimpleHttpDownloader(url, method);
        System.out.println(downloader.download());
    }

    @Test
    public void testDownloadCNKI() {
        String url = "https://www.cnki.net/";
        String method = "GET";
        DownloaderInterface downloader = new SimpleHttpDownloader(url, method);
        System.out.println(downloader.download());
    }

}
