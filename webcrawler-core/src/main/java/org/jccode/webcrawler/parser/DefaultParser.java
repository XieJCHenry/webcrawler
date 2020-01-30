package org.jccode.webcrawler.parser;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
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

    // cssSelector

    public DefaultParser() {
    }

//    public DefaultParser regex(String patternStr) {
//        patternString = Pattern.compile(patternStr);
//        return this;
//    }

    private Pattern pattern;
    private String cxxSelector;

    public DefaultParser regex(String pattern) {
        this.pattern = Pattern.compile(pattern);
        return this;
    }

    public DefaultParser selector(String cxxSelector) {
        this.cxxSelector = cxxSelector;
        return this;
    }

//    public List<String> all(List<WebPage> webPages) {
//        if (patternString != null) {
//            return regexAll(webPages);
//        } else if (cxxSelector != null) {
//            return selectorAll(webPages);
//        } else {
//            return defaultAll(webPages);
//        }
//    }
//
//    private List<String> defaultAll(List<WebPage> webPages) {
//        List<String> res = new ArrayList<>(8);
//        for (WebPage page : webPages) {
//            res.add(page.getContext());
//        }
//        return res;
//    }
//
//    private List<String> regexAll(List<WebPage> webPages) {
//        List<String> res = new ArrayList<>(8);
//        for (WebPage page : webPages) {
//            Matcher matcher = patternString.matcher(page.getContext());
//            boolean found = matcher.find();
//            while (found) {
//                res.add(matcher.group(1));
//                found = matcher.find();
//            }
//        }
//        return res;
//    }

//    public List<ResultItem> all(List<WebPage> webPages){
//
//        for (WebPage page : webPages) {
//
//        }
//    }

    /**
     * 还需完善
     *
     * @param webPages
     * @return
     * @Description TODO
     */
    private List<String> selectorAll(List<WebPage> webPages) {
        List<String> res = new ArrayList<>(8);
        for (WebPage page : webPages) {
            Document document = Jsoup.parse(page.getContext());
            Elements elements = document.select(cxxSelector);
            res.addAll(elements.eachText());
        }
        return res;
    }

    /**
     * 还需完善
     *
     * @param webPages
     * @return
     * @Description TODO
     */
    private List<String> xpathAll(List<WebPage> webPages) {
        return Collections.emptyList();
    }

//    public static DefaultParser selector(String cssSelector) {
//
//        return null;
//    }


}
