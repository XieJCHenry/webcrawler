package org.jccode.webcrawler.parser;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DefaultParser
 * <p>
 * 用于解析文本，提取url等
 *
 * @Description TODO 是否有必要？
 * @Author jc-henry
 * @Date 2020/1/27 13:18
 * @Version 1.0
 **/
public class DefaultParser {

    private Pattern pattern;
    // cssSelector

    public DefaultParser() {
    }

    public DefaultParser regex(String patternStr) {
        pattern = Pattern.compile(patternStr);
        return this;
    }

//    public static DefaultParser selector(String cssSelector) {
//
//        return null;
//    }

    public ResultItem parse(WebPage webPage) {
        String context = webPage.getContext();
        Matcher matcher = pattern.matcher(context);
        boolean found = matcher.find();
        while (found) {
            // 结果
            found = matcher.find();
        }

        return null;
    }


}
