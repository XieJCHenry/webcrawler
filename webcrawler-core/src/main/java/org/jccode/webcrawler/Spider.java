package org.jccode.webcrawler;

import org.apache.commons.lang3.StringUtils;
import org.jccode.webcrawler.conts.HttpClientConstant;
import org.jccode.webcrawler.downloader.HttpClientDownloader;
import org.jccode.webcrawler.extractor.Extractor;
import org.jccode.webcrawler.extractor.LinksExtractor;
import org.jccode.webcrawler.extractor.RegexExtractor;
import org.jccode.webcrawler.model.*;
import org.jccode.webcrawler.persistence.ConsolePersistence;
import org.jccode.webcrawler.persistence.Persistence;
import org.jccode.webcrawler.scheduler.DefaultTaskScheduler;
import org.jccode.webcrawler.scheduler.Scheduler;
import org.jccode.webcrawler.spiders.SpiderStopStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jc-henry
 */
public class Spider implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Spider.class);
    private static final String SpiderName = Spider.class.getName();

    private HttpClientDownloader downloader;
    private HttpClientConfiguration configuration;
    private Persistence persistence;
    private Scheduler taskScheduler;
    private Extractor urlsExtractor;  // 解析url链接
    private RegexExtractor contentExtractor;  // 解析内容
    private SpiderStopStrategy stopStrategy;    // TODO 爬虫停止策略，暂时无用

    private ReentrantLock notifyLock = new ReentrantLock();
    private Condition newUrlsCondition = notifyLock.newCondition();
    //    private Condition noUrlsCondition = notifyLock.newCondition();
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AtomicInteger pageCount = new AtomicInteger(0);
    private AtomicInteger exceptedCount = new AtomicInteger(0); // 控制爬虫的停止

    private String host;
    private long sleepTime; // 当没有url时的等待时间
    private ProxyModel proxy;
    private String urlPattern;
    private List<String> contentPatternsList;
    private List<Task> seeds; // 初始任务列表

    private Spider() {
    }

    public static Spider create() {
        return new Spider();
    }

    public Spider setHost(String host) {
        this.host = host;
        if (configuration != null) {
            this.configuration.setHost(host);
        }
        return this;
    }

    public Spider setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public Spider setProxy(ProxyModel proxyModel) {
        this.proxy = proxyModel;
        return this;
    }

    public Spider setProxy(String host, int port) {
        this.proxy = new ProxyModel(host, port);
        return this;
    }

    public Spider setConfiguration(HttpClientConfiguration configuration) {
        this.configuration = configuration;
        if (StringUtils.isNotEmpty(host)) {
            this.configuration.setHost(host);
        }
        return this;
    }

    public Spider setPersistence(Persistence persistence) {
        this.persistence = persistence;
        return this;
    }

    public Spider setScheduler(Scheduler scheduler) {
        this.taskScheduler = scheduler;
        return this;
    }

    public Spider setUrlsPattern(String urlPattern) {
        if (StringUtils.isNotBlank(urlPattern)) {
            this.urlPattern = urlPattern;
        }
        return this;
    }

    // 若用户不显式调用该方法，将只会执行初始添加的任务
    public Spider setExceptedCount(int exceptedCount) {
        if (exceptedCount < 0)
            throw new IllegalArgumentException("exceptedCount must be zero or positive");
        this.exceptedCount.set(exceptedCount);
        return this;
    }

    public Spider addTasks(String... urls) {
        if (seeds == null) {
            this.seeds = new ArrayList<>(urls.length);
        }
        for (String url : urls) {
            this.seeds.add(new Task(url));
        }
        return this;
    }

    public Spider addTasks(Task... tasks) {
        if (seeds == null) {
            seeds = new ArrayList<>(tasks.length);
        }
        seeds.addAll(Arrays.asList(tasks));
        return this;
    }

    public Spider setContentPattern(String... contentPatterns) {
        if (contentPatterns.length != 0) {
            contentPatternsList = Collections.unmodifiableList(Arrays.asList(contentPatterns));
        }
        return this;
    }

    private void initComponent() {
        downloader = new HttpClientDownloader();
        if (proxy != null) {
            downloader.setProxy(proxy);
        }
        if (configuration == null) {
            configuration = HttpClientConfiguration.custom();
        }
        if (persistence == null) {
            persistence = new ConsolePersistence();
        }
        if (taskScheduler == null) {
            taskScheduler = new DefaultTaskScheduler();
            if (seeds != null) {
                for (Task task : seeds) {
                    taskScheduler.add(task);
                }
            } else if (host != null && !host.trim().isEmpty()) {
                taskScheduler.add(new Task(host));
            }
        }
        if (urlPattern == null) {
            urlsExtractor = LinksExtractor.create();
        } else {
            urlsExtractor = RegexExtractor.create().addPattern("LinksPattern", urlPattern);
        }
        if (contentPatternsList.isEmpty()) {
            contentExtractor = null;
        } else {
            contentExtractor = RegexExtractor.create();
            for (String s : contentPatternsList) {
                contentExtractor.addPattern(s);
            }
        }
        if (seeds != null && exceptedCount.get() == 0) {
            exceptedCount.set(seeds.size());
        }
        if (sleepTime == 0) {
            sleepTime = HttpClientConstant.Time.DEFAULT_SLEEP_TIMES * 10;
        }
    }


    @Override
    public void run() {
        initComponent();
        isRunning.set(true);
        logger.info("Spider {} start !", SpiderName);
        while (!Thread.interrupted() && isRunning()) {
            final Task task = taskScheduler.poll();
            if (task == null) {
                if (!isRunning() || pageCount.get() == exceptedCount.get()) {
                    stop();
                    break;
                }
                waitNewUrl();
            } else {
                try {
                    executeTask(task);
                } catch (Exception e) {
                    logger.error("Fail : Exception occurred while executing task {}:{}",
                            task.getUrl(), e.getMessage());
                } finally {
                    signalNewUrl();
                    pageCount.incrementAndGet();
                }
            }
        }

    }

    private void stop() {
        if (isRunning.get()) {
            isRunning.set(false);
            taskScheduler.clear();
            downloader.close();
            logger.info("Spider stop success, fetch page count = {}", pageCount.get());
        } else {
            logger.warn("Spider stop fail");
        }

    }
//
//    /**
//     * @param exceptedCount page count that user except to download;
//     * @return
//     */
//    public Spider stop(int exceptedCount) {
//
//        return this;
//    }
//
//    /**
//     * TODO 以后再完成
//     *
//     * @param duration how long can the spider can run
//     * @return
//     */
//    public Spider stop(long duration) {
//        throw new UnsupportedOperationException();
//    }

    public Spider setStopStrategy(SpiderStopStrategy stopStrategy) {
        this.stopStrategy = stopStrategy;
        return this;
    }


    private boolean isRunning() {
        return isRunning.get();
    }

    private void waitNewUrl() {
        notifyLock.lock();
        try {
            if (isRunning() && pageCount.get() == exceptedCount.get()) {
                return;
            }
            newUrlsCondition.await(sleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("waitNewUrl interrupted {}", e);
        } finally {
            notifyLock.unlock();
        }
    }

    private void signalNewUrl() {
        notifyLock.lock();
        try {
            newUrlsCondition.signalAll();
        } finally {
            notifyLock.unlock();
        }
    }

    private void executeTask(Task task) {
        if (task.isUseProxy()) {
            downloader.setProxy(proxy);
        }
        WebPage webPage = downloader.download(task, configuration);
        if (webPage.isDownloadSuccess()) {
            onDownloadSuccess(webPage);
        } else {
            onDownloadFailed(task);
        }
    }

    private void onDownloadSuccess(WebPage webPage) {
        ResultItem resultItem = processPage(webPage);
        if (persistence instanceof ConsolePersistence) {
            persistence.process(resultItem);
        } else {
            new ConsolePersistence().process(resultItem);
            persistence.process(resultItem);
        }
    }

    private void onDownloadFailed(Task task) {
        if (task.getRetryTimes() == 0) {
            logger.warn("Task {} executed failed at all.", task.getUrl());
        } else {
            logger.info("Task {} executed failed, it will be added to queue again.", task.getUrl());
            taskScheduler.add(task);
        }
    }

    private ResultItem processPage(WebPage webPage) {
        List<String> urlList = urlsExtractor.extractList(webPage.getRawText());
        List<Task> taskList = new ArrayList<>(urlList.size());
        for (String url : urlList) {
            Task task = new Task(url);
            String host = task.getHost();
            for (Task seed : seeds) {
                if (host.equals(seed.getHost()) && seed.isUseProxy()) {
                    task.setUseProxy(true);
                    break;
                }
            }
        }
        taskScheduler.addList(taskList);
        //TODO 应该将内容解析交由用户处理
        Map<String, List<String>> extractMap = contentExtractor.extractMap(webPage.getRawText());
        ResultItem resultItem = ResultItem.custom();
        resultItem.setWebPage(webPage);
        resultItem.setResultFields((Collection) extractMap);

        return resultItem;
    }

}
