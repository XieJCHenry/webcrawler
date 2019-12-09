package org.jccode.webcrawler.downloader;

/**
 * HttpClientDownloaderBuilder2
 * <p>
 * 只创建两个实例，一个使用Proxy，一个不使用Proxy
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/8 17:00
 * @Version 1.0
 **/
public class HttpClientDownloaderBuilder2 {

    private static volatile HttpClientDownloaderBuilder2 INSTANCE;

    private HttpClientDownloaderBuilder2() {
    }

    public static HttpClientDownloaderBuilder2 create() {
        if (INSTANCE == null) {
            synchronized (HttpClientDownloaderBuilder2.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpClientDownloaderBuilder2();
                }
            }
        }
        return INSTANCE;
    }



}
