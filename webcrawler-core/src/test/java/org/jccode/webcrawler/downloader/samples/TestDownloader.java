package org.jccode.webcrawler.downloader.samples;

import org.apache.http.HttpHost;
import org.jccode.webcrawler.downloader.MultiTasksHttpDownloader;
import org.jccode.webcrawler.model.Task;
import org.junit.Test;

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
}
