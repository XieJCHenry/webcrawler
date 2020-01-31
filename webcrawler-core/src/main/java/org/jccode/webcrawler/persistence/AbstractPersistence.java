package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;

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

//    protected abstract void process(ResultItem resultItem);
}
