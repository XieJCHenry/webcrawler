package org.jccode.webcrawler.persistence;

import org.jccode.webcrawler.model.ResultItem;

import java.util.List;

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
     * @param resultItems
     */
    void process(List<ResultItem> resultItems);
}
