package org.jccode.webcrawler.conts;

/**
 * HttpConstant
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/11/30 19:33
 * @Version 1.0
 **/
public class HttpConstant {

    public static class Header{
        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; " +
                "Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
                "Safari/537.36";

        public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

        public static final String ACCEPT_LANGUAGE = "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7";

        public static final String DNT = "1";

        public static final String ACCEPT_ENCODING ="gzip, deflate";

        public static final String CONNECTION = "Keep-Alive";
    }

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; " +
            "Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
            "Safari/537.36";

    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";

    public static final String ACCEPT_LANGUAGE = "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7";

    public static final String DNT = "1";

    public static final String ACCEPT_ENCODING ="gzip, deflate";

    public static final String CONNECTION = "Keep-Alive";


//    public static class ContentType {
//        public static final String PLAIN_TEXT = "";
//        public static final String FORM = "";
//        public static final String JSON = "";
//    }

    private HttpConstant() {
    }
}
