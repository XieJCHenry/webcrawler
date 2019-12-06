package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.jccode.webcrawler.model.ResultItem;

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

    protected ResultItem resultItem;

    public AbstractPersistence() {
    }

    public AbstractPersistence(ResultItem resultItem) {
        this.resultItem = resultItem;
    }

    /**
     * It is no need for subClasses to override this method.
     *
     * @param resultItem
     */
    @Override
    public void process(ResultItem resultItem) {
        if (this.resultItem != null) {
            process();
        } else {
            // 如果方法参数resultItem也为null，由子类去检查并抛异常
            this.resultItem = resultItem;
            process();
        }
    }

    protected abstract void process();
}
