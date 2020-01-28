package org.jccode.webcrawler.downloader.samples;

import lombok.ToString;
import org.apache.http.HttpHost;
import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.downloader.MultiTasksHttpDownloader;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.junit.Test;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * TestDownloader
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/5 20:02
 * @Version 1.0
 **/
public class TestDownloader {

    @Test
    public void testMultiTaskDownload() throws InterruptedException {
        Task[] tasks = new Task[]{
                new Task("https://www.google.com/"),
                new Task("https://www.baidu.com"),
                new Task("https://cn.bing.com")
        };
        HttpHost proxy = new HttpHost("127.0.0.1", 1080);
        MultiTasksHttpDownloader downloader = new MultiTasksHttpDownloader(proxy, tasks);
        downloader.download();
    }

    @Test
    public void testCheckUrlConnected() {
        String url = "https://cn.bing.com";
        try {
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            System.err.println("can not connect to :" + url);
        }
    }

    @Test
    public void testHttpClientDownloader() {
        Task[] tasks = new Task[]{
                new Task("https://www.google.com/", true),
                new Task("https://www.github.com/", true),
                new Task("https://www.baidu.com"),
                new Task("https://cn.bing.com")
        };
        HttpClientDownloader downloader = new HttpClientDownloader();
        List<WebPage> pages = downloader.download(tasks);
        pages.forEach(page -> {
            System.out.println("***************************************");
            System.out.println(page.getSite() + page.getPath());
            System.out.println(page.getContext().length());
        });
        downloader.close();
    }
}
