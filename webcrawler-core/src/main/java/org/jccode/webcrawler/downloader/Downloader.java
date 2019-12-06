package org.jccode.webcrawler.downloader;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.Task;

import java.util.List;

/**
 * @author jc-henry
 */
public interface Downloader {


//    ResultItem download(Task task);

    List<ResultItem> download(Task... tasks);
}
