package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WebPage
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/27 9:28
 * @Version 1.0
 **/
@Data
@ToString
@EqualsAndHashCode
public class WebPage {

    // url = site + "/" + path;

    private String site;
    private String path = "";
    private int status;
    private String context;
    private String encoding;
    private String contentType;
    private LocalDateTime time;

    public WebPage() {
        time = LocalDateTime.now();
    }

    private Pattern pattern;
    private String cxxSelector;

    public WebPage regex(String pattern) {
        this.pattern = Pattern.compile(pattern);
        return this;
    }

    public WebPage selector(String cxxSelector) {
        this.cxxSelector = cxxSelector;
        return this;
    }

    public List<String> all() {
        if (pattern != null) {
            return regexAll();
        } else if (cxxSelector != null) {
            return selectorAll();
        } else {
            return Collections.singletonList(context);
        }
    }

    private List<String> regexAll() {
        List<String> res = new ArrayList<>(8);
        Matcher matcher = pattern.matcher(context);
        boolean found = matcher.find();
        while (found) {
            res.add(matcher.group(1));
            found = matcher.find();
        }
        return res;
    }

    private List<String> selectorAll() {
        Document document = Jsoup.parse(context);
        Elements elements = document.select(cxxSelector);
        return elements.eachText();
    }

    private List<String> xpathAll() {
        return Collections.emptyList();
    }
}
