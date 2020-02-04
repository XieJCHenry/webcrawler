package org.jccode.webcrawler.persistence;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * ConsolePersistence
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/5 20:11
 * @Version 1.0
 **/
public class ConsolePersistence extends AbstractPersistence {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsolePersistence.class);

    private static final String DELIMITER = ", ";

    @Override
    public void process(ResultItem resultItem) {
        WebPage webPage = resultItem.getWebPage();
        StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add("url = " + webPage.getPath());
        joiner.add("status = " + webPage.getStatus());
        joiner.add("title = " + webPage.getTitle());
        joiner.add("contentLength = " + webPage.getContentLength());
        joiner.add("contentType = " + webPage.getContentType());
        joiner.add("charSet = " + webPage.getCharSet());
        joiner.add("downTime = " + webPage.getTime());
        joiner.add("resultFields = {" + resultItem.getResultFields().toString() + "}");
        resultItem.setPersistSuccess(true);
        resultItem.setProcessTime(LocalDateTime.now());
        logger.info("ResultItem = [{}]", joiner.toString());
    }
}
