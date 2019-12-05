package org.jccode.webcrawler.persistence;

import org.jccode.webcrawler.model.ResultItem;

/**
 * Persistence
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 20:11
 * @Version 1.0
 **/
public interface Persistence {

    /**
     * 持久化接口
     *
     * @param resultItem
     */
    void process(ResultItem resultItem);
}
