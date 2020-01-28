package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.jccode.webcrawler.model.ResultItem;

import java.util.List;

/**
 * AbstractPersistence
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/6 15:56
 * @Version 1.0
 **/
@Getter
@Setter
public abstract class AbstractPersistence implements Persistence {


    /**
     * It is no need for subClasses to override this method.
     *
     * @param resultItem
     */
    @Override
    public void process(List<ResultItem> resultItem) {

    }

//    protected abstract void process(ResultItem resultItem);
}
