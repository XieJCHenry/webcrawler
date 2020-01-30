package org.jccode.webcrawler.downloader.samples;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.downloader.MultiTasksHttpDownloader;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
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
            System.out.println(page.getContent().length());
        });
        downloader.close();
    }

    @Test
    public void testExtractHeaders() throws IOException {
        Task[] tasks = new Task[]{
                new Task("https://www.baidu.com")
        };
        CloseableHttpClient client= HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(new HttpGet(tasks[0].getUrl()));
        System.out.println(response.getEntity().getContentType());
        System.out.println(response.getEntity().getContentEncoding());  // TODO 这里会取得null
        response.close();
        client.close();

    }
}
