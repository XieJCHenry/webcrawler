package org.jccode.webcrawler.persistence;

import org.apache.log4j.Logger;
import org.jccode.webcrawler.model.ResultItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ConsolePersistence
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/5 20:11
 * @Version 1.0
 **/
public class ConsolePersistence extends AbstractPersistence {

    private static final Logger logger = Logger.getLogger(ConsolePersistence.class);

    @Override
    public void process(List<ResultItem> resultItems) {
        for (ResultItem item : resultItems) {
            item.setPersistenceTime(LocalDateTime.now());
            logger.info("[" + item.getPersistenceTime() + "]" + item.getItemName() + "-"
                    + item.getUrl() + "-" + item.getStatus());
        }
    }
}
