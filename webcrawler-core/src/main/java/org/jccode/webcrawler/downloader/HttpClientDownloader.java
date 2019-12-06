package org.jccode.webcrawler.downloader;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.Task;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpClientDownloader
 * <p>
 * 根据传入的tasksList开启子线程进行下载
 *
 * @Description TODO 如何针对每个任务设置是否使用代理？
 * @Author jc-henry
 * @Date 2019/12/6 16:25
 * @Version 1.0
 **/
public class HttpClientDownloader extends AbstractHttpClientDownloader {

    private final Logger log = Logger.getLogger(HttpClientDownloader.class);

//    private static final int DEFAULT_THREAD_NUM =
//            Runtime.getRuntime().availableProcessors() + 1;
//
//    private static final int DEFAULT_MAX_THREAD_NUM = DEFAULT_THREAD_NUM * 2;
//
//    private static final long DEFAULT_KEEP_ALIVE_TIME = 30;
//
//    private static final String DEFAULT_THREAD_NAME = "HttpClientDownloader-Thread";

    private int threads;

    private ExecutorService executorService;

    private AtomicInteger threadAlive;

    private String threadName;

    private int threadNum;

    private CloseableHttpClient httpClient;

    private HttpHost proxy;

    private BlockingQueue<DownloaderTask> taskQueue = new LinkedBlockingQueue<>();

    private HttpClientDownloader() {
    }

    //    @Override
//    public List<ResultItem> download() {
//        return null;
//    }

    @Override
    public List<ResultItem> download(Task... tasks) {
        return null;
    }

    private void initComponnet() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadName)
                .setThreadFactory(Executors.defaultThreadFactory())
                .setUncaughtExceptionHandler((t, e) -> {
                    Logger log = Logger.getLogger(Thread.UncaughtExceptionHandler.class);
                    log.error("Thread terminated with exception:" + t.getName(), e);
                })
                .build();
//        this.executorService = new ThreadPoolExecutor(threadNum, DEFAULT_MAX_THREAD_NUM,
//                DEAFULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, threadFactory);
    }

    private void addTasks(List<Task> tasks) {
//        tasks.forEach(t);
    }
//
//    private boolean isProxyReliable() {
//
//    }


    static class DownloaderTask implements Callable<ResultItem> {

        private final Task task;
        private final CloseableHttpClient httpClient;
        private final HttpClientContext clientContext;


        DownloaderTask(Task task, CloseableHttpClient httpClient) {
            this.task = task;
            this.httpClient = httpClient;
            this.clientContext = HttpClientContext.create();
        }

        @Override
        public ResultItem call() throws Exception {
            if (task.useProxy()) {

            }
            return null;
        }

        public Task getTask() {
            return task;
        }

        public CloseableHttpClient getHttpClient() {
            return httpClient;
        }

        public HttpClientContext getClientContext() {
            return clientContext;
        }
    }


}
