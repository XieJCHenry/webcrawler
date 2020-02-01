package org.jccode.webcrawler.extractor;

import org.junit.Test;

/**
 * TestRegexExtractor
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 15:14
 * @Version 1.0
 **/
public class TestRegexExtractor {


    @Test
    public void testAddPattern() {
        RegexExtractor extractor = new RegexExtractor();
        String[] strings = new String[]{"123456", "79856", "13456", "123456"};
        for (String s : strings) {
            extractor.addPattern(s);
        }
        extractor.getRegexPatterns().forEach((name, pattern) -> {
            System.out.println(name + ":" + pattern.pattern());
        });
        System.out.println(extractor.getPatternNum());
    }
}
