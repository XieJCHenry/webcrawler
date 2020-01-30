package org.jccode.webcrawler.parse;

import org.jccode.webcrawler.conts.RegexPattern;
import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.junit.Test;

import java.util.List;

/**
 * TestParser
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/27 16:54
 * @Version 1.0
 **/
public class TestParser {

    @Test
    public void testRegex() {
        Task[] tasks = new Task[]{
//                new Task("https://www.google.com/", true),
//                new Task("https://www.github.com/", true),
//                new Task("https://www.baidu.com"),
//                new Task("https://cn.bing.com"),
                new Task("https://www.bilibili.com/")
        };
        HttpClientDownloader downloader = new HttpClientDownloader();
        List<WebPage> pages = downloader.download(tasks);
        pages.forEach(page -> {
            System.out.println("***************************************");
            System.out.println(page.getSite() + page.getPath());
//            List<String> list = page.regex(RegexPattern.URL.getPattern()).all();
//            for (String s : list) {
//                System.out.println("url = " + s);
//            }
        });
        downloader.close();
    }
}
