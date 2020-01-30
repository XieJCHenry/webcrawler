package org.jccode.webcrawler.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpUtil
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/7 15:36
 * @Version 1.0
 **/
public class HttpUtil {

    private static final String DEFAULT_REQUEST_URL = "https://www.google.com/";

    private HttpUtil() {
    }


    public static boolean isOk(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 200 && code < 300;
    }

    public static boolean isRedirect(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 300 && code < 400;
    }

    public static boolean isError(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        return code >= 400;
    }

    public static void printHeaders(HttpResponse response) {
        System.out.println("*****************************");
        for (Header header : response.getAllHeaders()) {
            System.out.println(header.toString());
        }
        System.out.println("*****************************");
    }

    public static Map<String, List<String>> convertHeaders(Header[] headers) {
        Map<String, List<String>> headersMap = new HashMap<>(headers.length);
        for (Header header : headers) {
            List<String> values = headersMap.computeIfAbsent(header.getName(), k -> new ArrayList<>());
            values.add(header.getValue());
        }
        return headersMap;
    }
}
