package org.jccode.webcrawler.conts;

/**
 * RegexPattern
 *
 * @Description
 * @Author jc-henry
 * @Date 2020/1/28 11:41
 * @Version 1.0
 **/
public enum RegexPattern {


    URL("<a href=\"(.+?)\""),
    IMG_URL("<img src=\"(.+?)\""),
    TEXT("<p>(.+?)</p>");

    private String pattern;

    private RegexPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
