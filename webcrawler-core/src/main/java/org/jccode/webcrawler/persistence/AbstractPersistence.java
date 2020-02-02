package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

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


    public void process(List<ResultItem> resultItems) {
        if (null != resultItems && !resultItems.isEmpty()) {
            for (ResultItem item : resultItems) {
                process(item);
            }
        }
    }
}
