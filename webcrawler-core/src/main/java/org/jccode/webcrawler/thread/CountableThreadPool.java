package org.jccode.webcrawler.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CountableThreadPool
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/6 20:24
 * @Version 1.0
 **/
public class CountableThreadPool {


    private static final int DEFAULT_THREAD_NUM = Runtime.getRuntime().availableProcessors() + 1;

//    private static final int DEFAULT_MAX_THREAD_NUM =

    private static final String DEFAULT_THREAD_NAME = "InternalHttpClientDownloader-Thread";

    private ExecutorService executorService;

    private AtomicInteger threadAlive = new AtomicInteger(0);

    private int threadNum;

    public CountableThreadPool() {
        this.threadName = DEFAULT_THREAD_NAME;
        this.threadNum = DEFAULT_THREAD_NUM;
    }

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
    }

    public CountableThreadPool(int threadNum, String threadName) {
        this.threadNum = threadNum;
        this.threadName = threadName;
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    private String threadName;

//    private void initThreadPoolExecutor() {
//        ThreadFactory threadFactory = new ThreadFactoryBuilder()
//                .setNameFormat(threadName)
//                .setThreadFactory(Executors.defaultThreadFactory())
//                .setUncaughtExceptionHandler((t, e) -> {
//                    Logger log = Logger.getLogger(Thread.UncaughtExceptionHandler.class);
//                    log.error("Thread terminated with exception:" + t.getName(), e);
//                })
//                .build();
//        ExecutorService executorService = new ThreadPoolExecutor(threadNum);
//    }

    public void execute() {

    }
}
