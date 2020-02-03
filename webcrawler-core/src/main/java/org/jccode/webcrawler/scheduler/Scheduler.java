package org.jccode.webcrawler.scheduler;

import org.jccode.webcrawler.model.Task;

/**
 * Url调度器，负责添加待访问的URL
 * <p>
 * 包含去重功能
 *
 * @author jc-henry
 */
public interface Scheduler {

    boolean add(Task task);
}
