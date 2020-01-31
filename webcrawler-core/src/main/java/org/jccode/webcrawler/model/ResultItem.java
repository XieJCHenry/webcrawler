package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ResultItem
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/31 22:17
 * @Version 1.0
 **/
@Getter
@ToString
public class ResultItem {

    @Setter
    private WebPage webPage;
    @Setter
    private boolean persistSuccess;
    @Setter
    private LocalDateTime processTime;

    public ResultItem(Map<String, Object> resultFields) {
        this(WebPage.emptyPage(), resultFields);
    }

    public ResultItem(WebPage webPage) {
        this(webPage, Collections.emptyMap());
    }

    public ResultItem(WebPage webPage, Map<String, Object> resultFields) {
        this.webPage = webPage;
        this.resultFields.putAll(resultFields);
    }

    private Map<String, Object> resultFields = new LinkedHashMap<>();

    public ResultItem putField(String key, Object object) {
        resultFields.put(key, object);
        return this;
    }

}
