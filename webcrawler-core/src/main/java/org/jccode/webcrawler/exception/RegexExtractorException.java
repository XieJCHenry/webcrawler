package org.jccode.webcrawler.exception;

/**
 * RegexExtractorException
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 22:30
 * @Version 1.0
 **/
public class RegexExtractorException extends RuntimeException {

    public static final String NO_PATTERN = "No Pattern has been registered in " +
            "RegexExtractor.";

    public static final String ONLY_ONE_PATTERN = "Count of regex pattern is over 1, " +
            "please make sure what pattern will be use.";

    public RegexExtractorException() {
    }

    public RegexExtractorException(String message) {
        super(message);
    }
}
