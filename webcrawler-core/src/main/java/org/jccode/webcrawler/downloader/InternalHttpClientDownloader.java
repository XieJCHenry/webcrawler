package org.jccode.webcrawler.downloader;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.jccode.webcrawler.model.Task;
import org.jccode.webcrawler.model.WebPage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * InternalHttpClientDownloader
 * <p>
 * 根据传入的tasksList开启子线程进行下载
 * <p>
 * <p>
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
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/6 16:25
 * @Version 1.0
 **/
public class InternalHttpClientDownloader extends AbstractHttpClientDownloader {

    private String threadName;
    private CloseableHttpClient httpClient;

    private ExecutorService executorService;
    private int threadNum;
    private int maxThreadNum;

    public InternalHttpClientDownloader(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.threadName = "InternalHttpClientDownloader-Thread";
        this.threadNum = 4;
        this.maxThreadNum = threadNum * 2;
        initExecutorService();
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


    @Override
    public List<WebPage> download(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        } else {
            BlockingQueue<DownloaderTask> tasksQueue =
                    new ArrayBlockingQueue<>(tasks.size());
            for (Task task : tasks) {
                tasksQueue.add(new DownloaderTask(task, httpClient));
            }
            List<WebPage> webPageList = new ArrayList<>(tasks.size());
            try {
                List<Future<WebPage>> futures = executorService.invokeAll(tasksQueue);
                for (Future<WebPage> future : futures) {
                    if (future.isDone()) {
                        webPageList.add(future.get());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            // 获得下载结果返回，将解析处理逻辑放到Parser中
            return webPageList;
        }
    }


    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    /*=====================================================================*/


    private void initExecutorService() {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat(threadName).build();
        this.executorService = new ThreadPoolExecutor(threadNum, maxThreadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                factory, new ThreadPoolExecutor.AbortPolicy());
    }

    /*=====================================================================*/

    static class DownloaderTask implements Callable<WebPage> {

        private final Task task;
        private final CloseableHttpClient httpClient;
        private final HttpClientContext clientContext;


        DownloaderTask(Task task, CloseableHttpClient httpClient) {
            this.task = task;
            this.httpClient = httpClient;
            this.clientContext = HttpClientContext.create();
        }

        @Override
        public WebPage call() throws Exception {
            // 调用httpClient.execute方法进行下载
            CloseableHttpResponse response = httpClient.execute(task.getRequest());
            WebPage webPage = initWebPage(response);
            response.close();
            return webPage;
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

        private WebPage initWebPage(CloseableHttpResponse response) throws IOException {
            WebPage webPage = new WebPage();
            String var1;
            webPage.setStatus(response.getStatusLine().getStatusCode());
            webPage.setSite(task.getHost());
            // set path
//            var1 = task.getUrl();
//            webPage.setPath(var1.substring(var1.indexOf(task.getHost() + 1)));
            // set content-type
            var1 = response.getFirstHeader("Content-Type").getValue();
            webPage.setContentType(var1.substring(0, var1.indexOf(";")));
            // set encoding
//            webPage.setEncoding(var1.substring(var1.indexOf("charset=" + 1)));
            // set context
            webPage.setContext(EntityUtils.toString(response.getEntity()));
            return webPage;
        }
    }


}
