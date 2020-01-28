package org.jccode.webcrawler.spider;

import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.parser.Parser;
import org.jccode.webcrawler.persistence.Persistence;

/**
 * Spider
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/28 13:52
 * @Version 1.0
 **/
public class Spider {

    private HttpClientDownloader downloader;
    private Persistence persistence;
    private Parser pageParser;

}
