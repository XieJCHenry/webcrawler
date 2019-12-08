package org.jccode.webcrawler.persistence;

import org.jccode.webcrawler.model.ResultItem;

/**
 * ConsolePersistence
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 20:11
 * @Version 1.0
 **/
public class ConsolePersistence extends AbstractPersistence {


    public ConsolePersistence(ResultItem resultItem) {
        super(resultItem);
    }

    @Override
    protected void process() {
        if (this.resultItem == null) {
            throw new NullPointerException("ResultItem is null");
        }
        StringBuilder appender = new StringBuilder();
        appender.append("url:").append(resultItem.getUrl()).append("\n");
        appender.append("status code:").append(resultItem.getStatus()).append("\n");
        appender.append("raw text:").append("\n").append(resultItem.getRawText()).append("\n");
        System.out.println(appender.toString());
    }
}
