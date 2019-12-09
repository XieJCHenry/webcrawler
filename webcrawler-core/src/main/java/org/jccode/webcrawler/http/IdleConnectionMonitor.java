package org.jccode.webcrawler.http;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.*;

/**
 * IdleConnectionMonitor
 * <p>
 * 监视PoolingConnectionManager中的连接是否过期，自动清除
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/8 17:13
 * @Version 1.0
 **/
public class IdleConnectionMonitor extends Thread {

//    private final ExecutorService executorService;
//
//    {
//        ThreadFactory threadFactory = new ThreadFactoryBuilder()
//                .setNameFormat("IdleConnectionMonitor")
//                .build();
//        executorService = Executors.newSingleThreadExecutor(threadFactory);
//    }
//
//    static class IdleConnectionMonitorThread implements Runnable {
//
//
//
//        @Override
//        public void run() {
//
//        }
//    }

    private final long DEFAULT_WAIT = 5000;
    private final int DEFAULT_IDLE_TIMEOUT = 30;
    private final String DEFAULT_THREAD_NAME = "IdleConnectionMonitorThread";

    private final HttpClientConnectionManager manager;
    private volatile boolean shutdown;
    private long wait;
    private int idleTimeOut;

    public IdleConnectionMonitor(HttpClientConnectionManager manager) {
        super();
        this.manager = manager;
        this.wait = DEFAULT_WAIT;
        this.idleTimeOut = DEFAULT_IDLE_TIMEOUT;
        this.setName(DEFAULT_THREAD_NAME);
    }

    public IdleConnectionMonitor(HttpClientConnectionManager manager, long wait,
                                 int idleTimeOut, String threadName) {
        super();
        this.manager = manager;
        this.wait = wait;
        this.idleTimeOut = idleTimeOut;
        this.setName(threadName);
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                wait(5000);
                manager.closeExpiredConnections();
                manager.closeIdleConnections(30, TimeUnit.SECONDS);
            }
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * 当程序被关闭时，调用此方法关闭该监视线程
     */
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
