package org.jccode.webcrawler.extractor;

import java.util.List;

/**
 * AbstractExtractor
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 15:37
 * @Version 1.0
 **/
public abstract class AbstractExtractor implements Extractor {

    @Override
    public String extract(String html) {
        return null;
    }

    @Override
    public List<String> extractList(String html) {
        return null;
    }
}
