package org.jccode.webcrawler.extractor;

import org.jccode.webcrawler.model.WebPage;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WebPage Extractor
 */
public interface Extractor {

    /**
     * @param html
     * @return
     */
    String extract(String html);

    /**
     * @param html
     * @return
     */
    List<String> extractList(String html);

    Map<String, List<String>> extractMap(String html);

    Set<List<String>> extractSet(String html);
}
