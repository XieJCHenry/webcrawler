package org.jccode.webcrawler.downloader;

public interface DownloaderInterface {

    String download();

    void setThread(int threads);
}
