package org.jccode.webcrawler.cmd;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * TestDemo
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/4 19:25
 * @Version 1.0
 **/
public class TestDemo {

    @Test
    public void testParseHostAddress() throws UnknownHostException {
        String url = "cn.bing.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.google.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.baidu.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.wikipedia.org";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));
    }

}
