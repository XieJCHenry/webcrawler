package org.jccode.webcrawler.parser;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

/**
 * Parser
 * <p>
 * 页面解析器，整合正则表达式，CSS选择器
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/11/30 19:19
 * @Version 1.0
 **/
public interface Parser {

    ResultItem parse(WebPage webPage);
}
