package org.jccode.webcrawler.extractor;

import org.jccode.webcrawler.conts.RegexPattern;
import org.jccode.webcrawler.util.CollectionUtils;
import org.jccode.webcrawler.util.RegexUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * LinksExtractor
 * <p>
 * extract all urls from a web page.
 *
 * @Description
 * @Author jc-henry
 * @Date 2020/2/1 20:19
 * @Version 1.0
 **/
public class LinksExtractor extends SinglePatternExtractor {

    public static final String PATTERN_STRING = RegexPattern.URL.pattern();
    public static final Pattern URL_PATTERN = Pattern.compile(PATTERN_STRING);

    private LinksExtractor() {
    }

    public static LinksExtractor create() {
        return new LinksExtractor();
    }

    public List<String> all(String html) {
        return extractList(html);
    }

    @Override
    public String extract(String html) {
        throw new UnsupportedOperationException("LinksExtractor only extract all " +
                "urls from a web page");
    }

    @Override
    public List<String> extractList(String html) {
        int count = RegexUtils.count(URL_PATTERN, html);
        if (count == 0) {
            return Collections.emptyList();
        } else {
            List<String> resList = new ArrayList<>(count);
            for (Matcher matcher = URL_PATTERN.matcher(html); matcher.find(); ) {
                resList.add(matcher.group());
            }
            return resList;
        }
    }

    public List<String> extractNoDuplicateList(String html) {
        int count = RegexUtils.count(URL_PATTERN, html);
        if (count == 0) {
            return Collections.emptyList();
        } else {
            List<String> resList = new ArrayList<>(count);
            for (Matcher matcher = URL_PATTERN.matcher(html); matcher.find(); ) {
                resList.add(matcher.group());
            }
            CollectionUtils.removeDuplicate(resList);
            return resList;
        }
    }

    /**
     * 借助StreamAPI解决去重问题
     *
     * @param html
     * @return
     */
    private List<String> removeDuplicate(String html) {
        Matcher matcher = URL_PATTERN.matcher(html);
        boolean found = matcher.find();
        int count = 0;
        while (found) {
            count++;
            found = matcher.find();
        }
        matcher.reset();
        found = matcher.find();
        List<String> resList = new ArrayList<>(count);
        while (found) {
            resList.add(matcher.group());
            found = matcher.find();
        }
        return resList.stream().distinct().collect(Collectors.toList());
    }

}
