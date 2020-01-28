package org.jccode.webcrawler.downloader;

import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * AbstractHttpClientDownloader
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/6 16:54
 * @Version 1.0
 **/
public abstract class AbstractHttpClientDownloader implements Downloader {

    protected static final int TEST_CONNECT_TIMEOUT = 5000;

//    public abstract List<ResultItem> download();

//    protected BlockingQueue<Task> tasksQueue = new LinkedBlockingQueue<>();


    @Override
    public abstract List<WebPage> download(List<Task> taskList);

    @Override
    public abstract void close();

    /**
     * 检查连接是否可用，如果是墙外的，则需要调用代理
     *
     * @param url
     * @return
     */
    protected boolean checkConnectionValidity(String url) {
        try {
            return checkConnectionValidity(new URL(url));
        } catch (MalformedURLException ignore) {
            // 如果 url 格式不对
            return false;
        }
    }

    protected boolean checkConnectionValidity(URL url) {
        int code;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(TEST_CONNECT_TIMEOUT);
            code = con.getResponseCode();
            con.disconnect();
            return code >= 200 && code < 300;
        } catch (IOException ignore) {
            if (con != null) {
                con.disconnect();
            }
            return false;
        }
    }
}
