package org.jccode.webcrawler.scheduler;

import com.google.common.collect.ConcurrentHashMultiset;
import org.jccode.webcrawler.model.Task;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PriorityScheduler
 * <p>
 * 参考webmagic-core的PriorityScheduler的实现
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/3 20:15
 * @Version 1.0
 **/
public class PriorityScheduler extends AbstractScheduler {

    private static final int INIT_CAPACITY = 8;
    // priority = 0
    private PriorityBlockingQueue<Task> noPriorityTasksQueue =
            new PriorityBlockingQueue<>();
    // priority > 0
    private PriorityBlockingQueue<Task> plusPriorityTasksQueue =
            new PriorityBlockingQueue<>(INIT_CAPACITY,
                    Comparator.comparingInt(Task::getPriority));
    // priority < 0
    private PriorityBlockingQueue<Task> minusPriorityTasksQueue =
            new PriorityBlockingQueue<>(INIT_CAPACITY,
                    Comparator.comparingInt(Task::getPriority));
    private ConcurrentHashMultiset<Task> visitedPool = ConcurrentHashMultiset.create();
    private AtomicInteger currentTasksCount = new AtomicInteger(0);
    private AtomicInteger visitedCount = new AtomicInteger(0);
    private ReentrantLock pollTaskLock = new ReentrantLock();
    private ReentrantLock newTaskLock = new ReentrantLock();

    @Override
    public int addList(List<Task> taskList) {
        AtomicInteger addCount = new AtomicInteger();
        if (taskList != null) {
            newTaskLock.lock();
            try {
                if (!taskList.isEmpty()) {
                    for (Task task : taskList) {
                        if (task.isExecuteSuccess() && visitedPool.add(task)) {
                            visitedCount.getAndIncrement();
                        } else {
                            pushTask(task);
                            addCount.incrementAndGet();
                        }
                    }
                }
            } finally {
                newTaskLock.unlock();
            }
        }
        currentTasksCount.getAndAdd(addCount.get());
        return addCount.get();
    }

    @Override
    public boolean add(Task task) {
        if (task.isExecuteSuccess() && visitedPool.add(task)) {
            visitedCount.incrementAndGet();
            return false;
        } else {
            pushTask(task);
            currentTasksCount.incrementAndGet();
            return true;
        }
    }

    private void pushTask(Task task) {
        if (task.getPriority() == 0) {
            noPriorityTasksQueue.add(task);
        } else if (task.getPriority() > 0) {
            plusPriorityTasksQueue.add(task);
        } else {
            minusPriorityTasksQueue.add(task);
        }
    }

    public int pushList(List<Task> taskList) {
        return addList(taskList);
    }

    public boolean push(Task task) {
        return add(task);
    }

    public Task poll() {
        Task task = null;
        pollTaskLock.lock();
        try {
            task = pollTask();
        } finally {
            pollTaskLock.unlock();
        }
        return task;
    }

    private Task pollTask() {
        if (!plusPriorityTasksQueue.isEmpty()) {
            return plusPriorityTasksQueue.poll();
        } else if (!noPriorityTasksQueue.isEmpty()) {
            return noPriorityTasksQueue.poll();
        } else if (!minusPriorityTasksQueue.isEmpty()) {
            return minusPriorityTasksQueue.poll();
        } else {
            return null;
        }
    }

    public int current() {
        return currentTasksCount.get();
    }

    public int visited() {
        return visitedCount.get();
    }
}
