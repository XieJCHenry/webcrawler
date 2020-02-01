package org.jccode.webcrawler.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * NoPatternsException
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 22:25
 * @Version 1.0
 **/
public class NoPatternsException extends RuntimeException {

    private static String DEFAULT_MESSAGE ="No Patterns registered in RegexExtractor.";

    public NoPatternsException(String message) {
        super(StringUtils.isBlank(message)?DEFAULT_MESSAGE:message);

    }

    public NoPatternsException() {
        super(DEFAULT_MESSAGE);
    }
}
