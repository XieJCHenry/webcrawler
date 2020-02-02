package org.jccode.webcrawler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegexUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/2 12:15
 * @Version 1.0
 **/
public class RegexUtils {
    private RegexUtils() {
    }

    /**
     * 该方法参考自 Hutool
     * @param pattern
     * @param content
     * @return
     */
    public static int count(Pattern pattern, String content) {
        if (null != pattern && null != content) {
            int count = 0;
            for (Matcher matcher = pattern.matcher(content); matcher.find(); count++) ;
            return count;
        } else {
            return 0;
        }
    }
}
