package org.jccode.webcrawler.downloader;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.Task;

/**
 * @author jc-henry
 */
public interface Downloader {


    /**
     * 下载
     *
     * @param task
     * @return
     */
    ResultItem download(Task task);
}
