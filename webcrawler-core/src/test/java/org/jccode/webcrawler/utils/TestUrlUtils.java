package org.jccode.webcrawler.utils;

import org.jccode.webcrawler.util.UrlUtils;
import org.junit.Test;

/**
 * TestUrlUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 13:01
 * @Version 1.0
 **/
public class TestUrlUtils {

    @Test
    public void testExtractHost() {
        String url = "https://www.baidu.com/search?q=apache";
        System.out.println(UrlUtils.extractHost(url));
        System.out.println(UrlUtils.removeProtocol(url));
    }
}
