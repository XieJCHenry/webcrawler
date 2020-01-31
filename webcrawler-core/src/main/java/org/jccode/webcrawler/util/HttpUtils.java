package org.jccode.webcrawler.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;

/**
 * HttpUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/7 15:36
 * @Version 1.0
 **/
public class HttpUtils {

    private static final String DEFAULT_REQUEST_URL = "https://www.google.com/";

    private HttpUtils() {
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

    public static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> headersMap = new LinkedHashMap<>(headers.length);
        for (Header header : headers) {
            headersMap.putIfAbsent(header.getName(), header.getValue());
        }
        return Collections.unmodifiableMap(headersMap);
    }


    @Deprecated
    public static String getContentType(String contentType) {
        int i;
        return ((i = contentType.indexOf(";")) != -1) ?
                contentType.substring(0, i) : "";
    }

    public static String getContentType(Header[] headers) {
        for (Header header : headers) {
            if ("Content-Type".equalsIgnoreCase(header.getName())) {
                return header.getValue();
            }
        }
        return HttpConstant.ContentType.UNKNOWN;
    }

    public static String getCharset(String html) {
        return Jsoup.parse(html).charset().name();
    }


    public static String getTitle(String html) {
        return Jsoup.parse(html).title();
    }

    public static String getTitle(Task task) {
        String url = task.getUrl();
        int var1 = url.lastIndexOf("/");
        int var2 = url.lastIndexOf(".");
        if (var1 != -1 && var2 != -1 && var1 < var2) {
            return url.substring(var1 + 1, var2);
        }
        return UUID.randomUUID().toString();
    }

    public static String getContent(String html) {
        return Jsoup.parse(html).body().outerHtml();
    }

}
