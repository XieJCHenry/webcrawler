package org.jccode.webcrawler.scheduler;

import org.jccode.webcrawler.model.Task;

import java.util.List;

/**
 * AbstractScheduler
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/3 20:12
 * @Version 1.0
 **/
public abstract class AbstractScheduler implements Scheduler{

    public abstract int addList(List<Task> taskList);
}
