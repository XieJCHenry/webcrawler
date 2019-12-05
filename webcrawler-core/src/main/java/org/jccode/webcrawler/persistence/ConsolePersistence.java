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
public class ConsolePersistence implements Persistence {

    private ResultItem resultItem;

    public ConsolePersistence(ResultItem resultItem) {
        this.resultItem = resultItem;
    }

    @Override
    public void process(ResultItem resultItem) {
        StringBuilder appender = new StringBuilder();
        String charset = resultItem.getCharset();
        appender.append("url:").append(resultItem.getUrl()).append("\n");
        appender.append("status code:").append(resultItem.getStatus()).append("\n");
        appender.append("raw text:").append("\n").append(resultItem.getRawText()).append("\n");
        System.out.println(appender.toString());

    }
}
