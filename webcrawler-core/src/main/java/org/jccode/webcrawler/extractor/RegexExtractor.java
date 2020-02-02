package org.jccode.webcrawler.extractor;

import lombok.Data;
import org.jccode.webcrawler.exception.RegexExtractorException;
import org.jccode.webcrawler.util.RegexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @Description TODO 寻找其他正则类库
 * @Author jc-henry
 * @Date 2020/2/1 14:56
 * @Version 1.0
 **/
@Data
public class RegexExtractor implements Extractor {

    private static final Logger logger = LoggerFactory.getLogger(RegexExtractor.class);
    private static final String PATTERN_LABEL = "RegexPattern-";

    private AtomicInteger patternNum = new AtomicInteger(0);
    /**
     * key:该pattern解析得到的结果名，用于持久化时填入map
     */
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

    /*======================= Only One Pattern ===========================*/
    @Override
    public String extract(String html) {
        return extract(html, 0);
    }

    public String extract(String html, int group) {
        return extractResult(html, group);
    }

    @Override
    public List<String> extractList(String html) {
        return extractResults(html);
    }



    /*======================= Multi Patterns ===========================*/
    /**
     * 输入多个regex，返回每个regex对应的解析结果
     *
     * @param html
     * @return
     */
    @Override
    public Set<List<String>> extractSet(String html) {
        return extractSet(html, 0);
    }

    public Set<List<String>> extractSet(String html, int group) {
        Set<List<String>> resultSet = new LinkedHashSet<>();
        for (Map.Entry<String, Pattern> entry : regexPatterns.entrySet()) {
            resultSet.add(extractResults(html, entry.getValue(), group));
        }
        return resultSet; // unmodifiedSet 无法执行.clear()方法，因此不使用。下同。
    }

    /**
     * 根据用户输入的多条pattern，解析得到每条pattern对应的结果集List
     *
     * @param html
     * @return
     */
    @Override
    public Map<String, List<String>> extractMap(String html) {
        return extractMap(html, 0);
    }

    public Map<String, List<String>> extractMap(String html, int group) {
        if (regexPatterns.size() == 0) {
            throw new RegexExtractorException(RegexExtractorException.NO_PATTERN);
        }
        Map<String, List<String>> resultMap = new LinkedHashMap<>(regexPatterns.size());
        for (Map.Entry<String, Pattern> entry : regexPatterns.entrySet()) {
            List<String> resList = extractResults(html, entry.getValue(), group);
            resultMap.put(entry.getKey(), resList);
        }
        return resultMap;
    }

    /*======================= Helpers ===========================*/

    private String extractResult(String html, int group) {
        if (cachePattern == null || patternNum.get() > 1) {
            throw new RegexExtractorException(RegexExtractorException.ONLY_ONE_PATTERN);
        } else {
            Matcher matcher = cachePattern.matcher(html);
            return matcher.find() ? matcher.group(group) : "";
        }
    }

    private List<String> extractResults(String html) {
        if (cachePattern == null || patternNum.get() > 1) {
            throw new RegexExtractorException(RegexExtractorException.ONLY_ONE_PATTERN);
        } else {
            return extractResults(html, cachePattern, 0);
        }
    }

    private List<String> extractResults(String html, Pattern pattern, int group) {
        int count;
        if ((count = RegexUtils.count(pattern, html)) == 0) {
            return Collections.emptyList();
        } else {
            List<String> res = new ArrayList<>(count);
            for (Matcher matcher1 = pattern.matcher(html); matcher1.find(); ) {
                res.add(matcher1.group(group));
            }
            return res;
        }
    }
}
