package org.jccode.webcrawler.conts;

/**
 * HttpConstant
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/11/30 19:33
 * @Version 1.0
 **/
public class HttpConstant {

    public static class Header {
        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; " +
                "Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904" +
                ".108 " +
                "Safari/537.36";

        public static final String ACCEPT = "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,*/*;q=0.8";

        public static final String ACCEPT_LANGUAGE = "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0" +
                ".7";

        public static final String DNT = "1";

        public static final String ACCEPT_ENCODING = "gzip, deflate";

        public static final String CONNECTION = "Keep-Alive";
    }

    public static class ContentType {
        // 常见媒体格式
        public static final String TEXT_HTML = "text/html";
        public static final String TEXT_XML = "text/xml";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String IMAGE_GIF = "image/gif";
        public static final String IMAGE_JPEG = "image/jpeg";
        public static final String IMAGE_PNG = "image/png";
        // 以application开头的媒体格式
        public static final String APPLICATION_XML = "application/xml";
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_PDF = "application/pdf";
        public static final String APPLICATION_WORD = "application/msword"; // word文档
        public static final String APPLICATION_STREAM = "application/octet-stream";
        public static final String APPLICATION_FORM = "application/x-www-form" +
                "-urlencoded";  // 表单数据
        public static final String MULTIPART = "multipart/form-data";   // 上传数据

        public static final String UNKNOWN = "unknown";
    }

    private HttpConstant() {
    }
}
