package org.jccode.webcrawler.extractor;

import org.jccode.webcrawler.model.WebPage;

import java.util.List;

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
     *
     * @param html
     * @return
     */
    List<String> extractList(String html);
}
