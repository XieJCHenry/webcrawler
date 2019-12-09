package org.jccode.webcrawler.downloader;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.jccode.webcrawler.http.CustomResponseHandler;
import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * InternalHttpClientDownloader
 * <p>
 * 根据传入的tasksList开启子线程进行下载
 * <p>
 * TODO 如何针对每个任务设置是否使用代理？
 * 存在问题：
 * 对于每一个Task，都有一个useProxy的标志，这个标志如果为true，代表需要用到Proxy，
 * 否则不需要用Proxy。但问题在于，CloseableHttpClient创建后就无法改变，也就是说不能在
 * 程序运行时动态地设置是否使用代理，这是问题一。如果需要用到代理，我希望程序能直接使用系统本地
 * 代理，对于不同操作系统，检查系统本地代理的所使用的命令行是不同的，因此如何便捷地获取不同操作系统
 * 下的socks5代理，这是问题二。如果系统代理不可用，或者如果系统没有设置代理，对需要用到代理的Task，
 * 应该将该任务设置为执行失败，丢到一个集合中，打日志。
 * <p>
 * 解决方案：
 * 问题一：
 * 方案一：通过Builder创建两个Internal CloseableHttpClient，通过一个Wrapper包装起来。
 * 方案二：寻找一种可以动态设置代理的方法。
 * 选择方案一，理由：因为CloseableHttpClient一旦创建后，就无法再修改，只能execute请求.
 * 如果为了每一个任务都切换代理，使用反射的时间耗费带来的性能损失
 * 将会远大于创建两个CloseableHttpClient带来的空间占用。
 * <p>
 * 问题二：(暂时解决)
 * 方案一：对于不同操作系统，获取并检查系统代理有效性的流程是一致的，可以通过设计模式统一起来。
 * 问题三：
 * TODO 这是一个更大的架构问题，还不能很好地解决。待定。
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/6 16:25
 * @Version 1.0
 **/
public class InternalHttpClientDownloader extends AbstractHttpClientDownloader {

    private final Logger log = Logger.getLogger(InternalHttpClientDownloader.class);
    private CloseableHttpClient httpClient;
    private CustomResponseHandler responseHandler = new CustomResponseHandler();

    private ExecutorService executorService;
    // 其实并不需要持有一个任务队列的对象，只需要在download方法中传入待下载任务即可？
//    private BlockingQueue<DownloaderTask> taskQueue = new LinkedBlockingQueue<>();
    private AtomicInteger threadAlive = new AtomicInteger(0);
    private String threadName;
    private int threadNum;
    private int maxThreadNum;

    public InternalHttpClientDownloader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        generateExecutorService();
    }

    public InternalHttpClientDownloader(CloseableHttpClient httpClient,
                                        ExecutorService executorService) {
        this(httpClient);
        this.executorService = executorService;
    }

    public InternalHttpClientDownloader setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public InternalHttpClientDownloader setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    //    @Override
//    public List<ResultItem> download() {
//        return null;
//    }

    @Override
    public List<ResultItem> download(Task[] tasks) {
        if (tasks == null || tasks.length == 0) {
            return Collections.emptyList();
        } else {
            BlockingQueue<DownloaderTask> tasksQueue =
                    new ArrayBlockingQueue<>(tasks.length);
            for (Task task : tasks) {
                tasksQueue.add(new DownloaderTask(task, httpClient));
            }
            try {
                List<Future<ResultItem>> futures = executorService.invokeAll(tasksQueue);
                // 获得下载结果，解析或者返回
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return Collections.emptyList();
    }



    /*=====================================================================*/


    private void generateExecutorService() {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat(threadName).build();
        this.executorService = new ThreadPoolExecutor(threadNum, maxThreadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                factory, new ThreadPoolExecutor.AbortPolicy());
    }

    /*=====================================================================*/

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
            // 这里调用httpClient.execute方法进行下载
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
