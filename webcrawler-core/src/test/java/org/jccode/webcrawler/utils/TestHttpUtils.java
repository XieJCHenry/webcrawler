package org.jccode.webcrawler.utils;

import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.model.HttpClientConfiguration;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.jccode.webcrawler.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * TestHttpUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 20:22
 * @Version 1.0
 **/
public class TestHttpUtils {

    @Test
    public void testExtractCharset() throws IOException {
        Document document = Jsoup.parse(new URL("https://www.baidu.com/"), 5000);
//        System.out.println(document.charset().name());
//        System.out.println(document.head());
        System.out.println(document.body().outerHtml());

    }

    @Test
    public void testMP3FileHeaders() {
        String[] paths = new String[]{
                "http://music.163.com/song/media/outer/url?id=1352968920.mp3",
                "http://music.163.com/song/media/outer/url?id=1352968928.mp3",
                "http://music.163.com/song/media/outer/url?id=28391671.mp3"};
        Task task = new Task(paths[0]);
        HttpClientConfiguration configuration = HttpClientConfiguration.custom()
                .setHost(task.getHost());
        HttpClientDownloader downloader = new HttpClientDownloader().setProxy(null);
        WebPage page = downloader.download(task, configuration);
        Map<String, String> headers = page.getHeaders();
        headers.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        });
    }

}
