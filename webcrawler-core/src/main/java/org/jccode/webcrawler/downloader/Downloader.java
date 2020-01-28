package org.jccode.webcrawler.downloader;

import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;

import java.util.List;

/**
 * @author jc-henry
 */
public interface Downloader {


//    ResultItem download(Task task);

    List<WebPage> download(List<Task> taskList);

    void close();
}
