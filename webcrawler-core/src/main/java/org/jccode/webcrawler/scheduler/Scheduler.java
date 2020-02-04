package org.jccode.webcrawler.scheduler;

import org.jccode.webcrawler.model.Task;

import java.util.List;

/**
 * Url调度器，负责添加待访问的URL
 * <p>
 * 包含去重功能
 *
 * @author jc-henry
 */
public interface Scheduler {

    boolean add(Task task);

    int addList(List<Task> taskList);

    Task poll();

    default boolean push(Task task) {
        return add(task);
    }

    default int pushList(List<Task> taskList) {
        return addList(taskList);
    }

    int current();

    int visited();

    void clear();
}
