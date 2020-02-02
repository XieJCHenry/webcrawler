package org.jccode.webcrawler.extractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SinglePatternExtractor
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/2 13:30
 * @Version 1.0
 **/
public abstract class SinglePatternExtractor implements Extractor {

    @Override
    public Map<String, List<String>> extractMap(String html) {
        return Collections.emptyMap();
    }

    @Override
    public Set<List<String>> extractSet(String html) {
        return Collections.emptySet();
    }
}
