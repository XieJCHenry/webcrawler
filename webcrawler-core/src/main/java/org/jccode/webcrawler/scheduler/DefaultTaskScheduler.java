package org.jccode.webcrawler.scheduler;

import com.google.common.collect.ConcurrentHashMultiset;
import org.jccode.webcrawler.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DefaultTaskScheduler
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/3 20:14
 * @Version 1.0
 **/
public class DefaultTaskScheduler extends AbstractScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTaskScheduler.class);
    private static final String schedulerName = DefaultTaskScheduler.class.getName();

    private final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMultiset<Task> visitedPool = ConcurrentHashMultiset.create();
    private ReentrantLock newTaskLock = new ReentrantLock();
    private AtomicInteger visitedCount = new AtomicInteger(0);
    private AtomicInteger currentTaskCount = new AtomicInteger(0);

    @Override
    public int addList(List<Task> taskList) {
        AtomicInteger addCount = new AtomicInteger(0);
        if (taskList != null) {
            newTaskLock.lock();
            try {
                if (!taskList.isEmpty()) {
                    for (Task task : taskList) {
                        if (task.isExecuteSuccess() && visitedPool.add(task)) {
                            visitedCount.incrementAndGet();
                        } else {
                            taskQueue.add(task);
                            addCount.incrementAndGet();
                            logger.info("{} add task = {}", schedulerName, task.getUrl());
                        }
                    }
                }
            } finally {
                newTaskLock.unlock();
            }
        }
        currentTaskCount.getAndAdd(addCount.get());
        logger.info("{} adds {} new tasks.", schedulerName, addCount.get());
        return addCount.get();
    }

    @Override
    public boolean add(Task task) {
        if (task.isExecuteSuccess() && visitedPool.add(task)) {
            visitedCount.incrementAndGet();
            return false;
        } else {
            taskQueue.add(task);
            currentTaskCount.incrementAndGet();
            logger.info("{} add task = {}", schedulerName, task.getUrl());
            return true;
        }
    }

//    public boolean push(Task task) {
//        return add(task);
//    }
//
//    public int pushList(List<Task> taskList) {
//        return addList(taskList);
//    }

    public void clear() {
        synchronized (this) {
            if (!taskQueue.isEmpty()) {
                taskQueue.clear();
            }
            if (!visitedPool.isEmpty()) {
                visitedPool.clear();
            }
            currentTaskCount.set(0);
            visitedCount.set(0);
        }
    }

    public Task poll() {
        if (!taskQueue.isEmpty()) {
            currentTaskCount.decrementAndGet();
            return taskQueue.poll();
        }
        return null;
    }

    public int current() {
        return currentTaskCount.get();
    }

    public int visited() {
        return visitedCount.get();
    }
}
