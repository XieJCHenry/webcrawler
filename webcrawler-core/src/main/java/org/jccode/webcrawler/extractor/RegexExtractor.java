package org.jccode.webcrawler.extractor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jccode.webcrawler.exception.NoPatternsException;
import org.jccode.webcrawler.exception.RegexExtractorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegexExtractor
 * <p>
 * 功能实现：可同时传入多条regex，生成一个结果集；如果只传入一条regex，只生成特定的解析结果
 * <p>
 * 第一期：只使用jdk类库提供简单的正则功能，后期再引入其他类库，方便用户的正则表达式生成
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 14:56
 * @Version 1.0
 **/
@Data
public class RegexExtractor implements Extractor {

    private static final Logger logger = LoggerFactory.getLogger(RegexExtractor.class);
    private static final String PATTERN_LABEL = "RegexPattern-";

    private AtomicInteger patternNum = new AtomicInteger(0);
    private final Map<String, Pattern> regexPatterns = new LinkedHashMap<>();
    /**
     * 如果用户只输入一条pattern，将保存在此对象中
     */
    private Pattern cachePattern;
    private String cacheLabel;

    public RegexExtractor() {
    }

    public static RegexExtractor create() {
        return new RegexExtractor();
    }

    public RegexExtractor addPattern(String pattern) {
        if (patternNum.get() < 1) {
            cachePattern = Pattern.compile(pattern);
            cacheLabel = PATTERN_LABEL + patternNum.getAndIncrement();
        } else {
            String label = PATTERN_LABEL + patternNum.getAndIncrement();
            if (cachePattern != null) {
                regexPatterns.putIfAbsent(cacheLabel, cachePattern);
                cachePattern = null;
                cacheLabel = null;
            }
            regexPatterns.putIfAbsent(label, Pattern.compile(pattern));
        }
        return this;
    }

    /**
     * <p>
     * 不与<method>addPattern(String name, Pattern pattern)</method>合并。
     * 将Pattern.compile的执行延迟到最后一步
     *
     * @param name
     * @param patternStr
     * @return
     */
    public RegexExtractor addPattern(String name, String patternStr) {
        if (patternNum.get() < 1) {
            cachePattern = Pattern.compile(patternStr);
            cacheLabel = name + patternNum.getAndIncrement();
        } else {
            String label = name + patternNum.getAndIncrement();
            if (cachePattern != null) {
                regexPatterns.putIfAbsent(cacheLabel, cachePattern);
                cachePattern = null;
                cacheLabel = null;
            }
            regexPatterns.putIfAbsent(label, Pattern.compile(patternStr));
        }
        return this;
    }

    @Override
    public String extract(String html) {
        return extractResult(html);
    }

    @Override
    public List<String> extractList(String html) {
        return extractResults(html);
    }

    /**
     * 输入多个regex，返回每个regex对应的解析结果
     *
     * @param html
     * @return
     */
    public Set<List<String>> extractSet(String html) {
        Set<List<String>> resultSet = new LinkedHashSet<>();
        for (Map.Entry<String, Pattern> entry : regexPatterns.entrySet()) {
            resultSet.add(extractResults(html, entry.getValue()));
        }
        return resultSet; // unmodifiedSet 无法执行.clear()方法，因此不使用。下同。
    }

    /**
     * 根据用户输入的多条pattern，解析得到每条pattern对应的结果集List
     *
     * @param html
     * @return
     */
    public Map<String, List<String>> extractMap(String html) {
        if (regexPatterns.size() == 0) {
            throw new RegexExtractorException(RegexExtractorException.NO_PATTERN);
        }
        Map<String, List<String>> resultMap = new LinkedHashMap<>(regexPatterns.size());
        for (Map.Entry<String, Pattern> entry : regexPatterns.entrySet()) {
            List<String> resList = extractResults(html, entry.getValue());
            resultMap.put(entry.getKey(), resList);
        }
        return resultMap;
    }

    private String extractResult(String html) {
        if (cachePattern == null || patternNum.get() > 1) {
            throw new RegexExtractorException(RegexExtractorException.ONLY_ONE_PATTERN);
        } else {
            return extractResult(html, cachePattern);
        }
    }

    private String extractResult(String html, Pattern pattern) {
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    private List<String> extractResults(String html) {
        if (cachePattern == null || patternNum.get() > 1) {
            throw new RegexExtractorException(RegexExtractorException.ONLY_ONE_PATTERN);
        } else {
            return extractResults(html, cachePattern);
        }
    }

    private List<String> extractResults(String html, Pattern pattern) {
        List<String> res = new ArrayList<>();
        Matcher matcher = pattern.matcher(html);
        boolean found = matcher.find();
        if (!found) {
            return Collections.emptyList();
        }
        while (found) {
            res.add(matcher.group());
            found = matcher.find();
        }
        return res;
    }
}
