package org.jccode.webcrawler.spider;

import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.persistence.Persistence;

/**
 * Spider
 *
 * @Description TODO 爬虫主类
 * @Author jc-henry
 * @Date 2020/1/28 13:52
 * @Version 1.0
 **/
public class Spider {

//    private HttpClientDownloader downloader;
    private Persistence persistence;

    public Spider() {
//        downloader = new HttpClientDownloader();
    }

    public static Spider custom() {
        return new Spider();
    }

    public Spider persistence(Persistence persistence) {
        this.persistence = persistence;
        return this;
    }



}
