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


    URL("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"),
    IMG_URL("<img src=\"(.+?)\""),
    TEXT("<p>(.+?)</p>");

    private String pattern;

    private RegexPattern(String pattern) {
        this.pattern = pattern;
    }

    public String pattern() {
        return pattern;
    }
}
