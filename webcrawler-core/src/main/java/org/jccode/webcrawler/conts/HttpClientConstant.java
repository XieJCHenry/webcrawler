package org.jccode.webcrawler.conts;

/**
 * HttpClientConstant
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/30 14:49
 * @Version 1.0
 **/
public class HttpClientConstant {
    public static final class Time {
        public static final int DEFAULT_TIMEOUT = 3000;
        public static final int DEFAULT_THREAD_NUMS = Runtime.getRuntime().availableProcessors();
        public static final int DEFAULT_KEEP_ALIVE = 30 * 1000;
        public static final int DEFAULT_SLEEP_TIMES = 5000;
        public static final int DEFAULT_RETRY_TIMES = 3;
        public static final String DEFAULT_THREAD_NAME = "HttpClientDownloader-Thread";

    }
}
