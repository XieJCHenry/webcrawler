package org.jccode.webcrawler.downloader.samples;

import lombok.ToString;
import org.apache.http.HttpHost;
import org.jccode.webcrawler.downloader.MultiTasksHttpDownloader;
import org.jccode.webcrawler.model.Task;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TestDownloader
 *
 * @Description TODO
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
}
