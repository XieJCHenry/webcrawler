package org.jccode.webcrawler.downloader;

/**
 * AbstractDownloader
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 15:22
 * @Version 1.0
 **/
public abstract class AbstractDownloader implements DownloaderInterface {

    protected int threads;

    @Override
    public void setThread(int threads) {
        this.threads = (threads <= 0) ? 1 : threads;
    }


}
