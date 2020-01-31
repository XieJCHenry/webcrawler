package org.jccode.webcrawler.parser;

import org.jccode.webcrawler.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DefaultParser
 * <p>
 * 用于解析文本，提取url等
 *
 * @Description TODO 解析页面
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

    private List<Pattern> regexPattern;
    private String cxxSelector;





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
            Document document = Jsoup.parse(page.getContent());
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
