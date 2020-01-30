package org.jccode.webcrawler.utils;

import org.jccode.webcrawler.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

}
