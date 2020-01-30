package org.jccode.webcrawler.parser;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AbstractParser
 * <p>
 * 《设计模式》
 * 模板方法：定义一个不可变的算法框架，让子类去实现框架中的具体细节
 *
 * @Description
 * @Author jc-henry
 * @Date 2020/1/28 15:53
 * @Version 1.0
 **/
public abstract class AbstractParser /*implements Parser*/ {

    protected String patternString;


    public final List<ResultItem> all(List<WebPage> webPages) {
        List<ResultItem> res = new ArrayList<>(webPages.size());
        for (WebPage page : webPages) {
            res.addAll(single(page, parse(page.getContext())));
        }
        return res;
    }

    /**
     * 可拓展为：正则解析，css选择器解析，XPath解析
     *
     * @param context
     * @return
     */
    protected abstract List<String> parse(String context);

    private List<ResultItem> single(WebPage webPage, List<String> extracts) {
        if (extracts.size() <= 1) {
            return Collections.singletonList(initItem(webPage, webPage.getContext()));
        } else {
            List<ResultItem> itemList = new ArrayList<>(extracts.size());
            for (String extract : extracts) {
                itemList.add(initItem(webPage, extract));
            }
            return itemList;
        }
    }

    private ResultItem initItem(WebPage webPage, String extract) {
        ResultItem item = new ResultItem();
        item.setCharSet(webPage.getEncoding())
                .setUrl(webPage.getSite() + "\\" + webPage.getPath())
                .setConserved(false)
                .setContext(extract)
                .setStatus(webPage.getStatus())
                .setContentType(webPage.getContentType());
        return item;
    }

}
