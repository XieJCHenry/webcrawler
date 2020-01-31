package org.jccode.webcrawler.downloader.samples;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.downloader.MultiTasksHttpDownloader;
import org.jccode.webcrawler.model.HttpClientConfiguration;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.jccode.webcrawler.persistence.FilePersistence;
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
    public void testDownloadHTML() {
        Task task = new Task("https://cn.bing.com");
        task = new Task("https://www.google.com");
        HttpClientConfiguration configuration = HttpClientConfiguration.custom()
                .setHost(task.getHost());
        HttpClientDownloader downloader = new HttpClientDownloader().setProxy(null);
        WebPage page = downloader.download(task, configuration);
//        String path = "";

        System.out.println(page);
//        downloader.close();
    }

    @Test
    public void testDownloadMP3() {
        String[] paths = new String[]{
                "http://music.163.com/song/media/outer/url?id=1352968920.mp3",
                "http://music.163.com/song/media/outer/url?id=1352968928.mp3"};
        Task task = new Task(paths[0]);
        HttpClientConfiguration configuration = HttpClientConfiguration.custom()
                .setHost(task.getHost());
        HttpClientDownloader downloader = new HttpClientDownloader().setProxy(null);
        WebPage page = downloader.download(task, configuration);
        FilePersistence filePersistence = new FilePersistence("D:\\test", "mp3");
        filePersistence.process(page);
    }

    @Test
    public void testExtractHeaders() throws IOException {
        Task[] tasks = new Task[]{
                new Task("https://www.baidu.com")
        };
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(new HttpGet(tasks[0].getUrl()));
        System.out.println(response.getEntity().getContentType());
        System.out.println(response.getEntity().getContentEncoding());
        response.close();
        client.close();
    }
}
