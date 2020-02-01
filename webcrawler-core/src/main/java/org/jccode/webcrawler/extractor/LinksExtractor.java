package org.jccode.webcrawler.extractor;

import com.google.common.collect.LinkedHashMultiset;

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
public class LinksExtractor implements Extractor {

    public static final String URL_PATTERN = "(https?|ftp|file)" +
            "://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
    public static final Pattern urlPattern = Pattern.compile(URL_PATTERN);

    @Override
    public String extract(String html) {
        throw new UnsupportedOperationException("LinksExtractor can only extract all " +
                "urls from a web page");
    }

    @Override
    public List<String> extractList(String html) {
        Matcher matcher = urlPattern.matcher(html);
        boolean found = matcher.find();
        int count = 0;
        while (found) {
            count++;
            found = matcher.find();
        }
        List<String> resList = new ArrayList<>(count);
        matcher.reset();
        found = matcher.find();
        while (found) {
            resList.add(matcher.group());
            found = matcher.find();
        }
        return Collections.unmodifiableList(resList);
    }

    public List<String> extractNoDuplicateList(String html) {
        Matcher matcher = urlPattern.matcher(html);
        boolean found = matcher.find();
        int count = 0;
        while (found) {
            count++;
            found = matcher.find();
        }
        matcher.reset();
        Set<String> helperSet = new LinkedHashSet<>(count);
        List<String> resList = new ArrayList<>(count);
        found = matcher.find();
        while (found) {
            helperSet.add(matcher.group());
            found = matcher.find();
        }
        resList.addAll(helperSet);
        helperSet.clear();
        return Collections.unmodifiableList(resList);
    }

    /**
     * 借助StreamAPI解决去重问题
     *
     * @param html
     * @return
     */
    private List<String> removeDuplicate(String html) {
        Matcher matcher = urlPattern.matcher(html);
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
