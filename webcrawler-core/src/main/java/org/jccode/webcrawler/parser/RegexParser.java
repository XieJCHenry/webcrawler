package org.jccode.webcrawler.parser;

import org.apache.commons.lang3.StringUtils;
import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RegexParser
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/28 15:58
 * @Version 1.0
 **/
public class RegexParser extends AbstractParser {

    private Pattern pattern;

    public RegexParser(String patternString) {
        this.pattern = StringUtils.isEmpty(patternString) ? null :
                Pattern.compile(patternString);
    }

    @Override
    protected List<String> parse(String context) {
        List<String> extracts = new ArrayList<>();
        Matcher matcher = pattern.matcher(context);
        boolean found = matcher.find();
        while (found) {
            extracts.add(matcher.group(1));
            found = matcher.find();
        }
        return extracts;
    }
}
