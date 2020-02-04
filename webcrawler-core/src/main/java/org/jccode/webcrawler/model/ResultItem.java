package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jccode.webcrawler.extractor.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * ResultItem
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/31 22:17
 * @Version 1.0
 **/
@Data
@ToString
public class ResultItem {

    private WebPage webPage;
    private boolean persistSuccess;
    private LocalDateTime processTime;
    @Getter
    private Collection resultFields;    // TODO resultFields如何设计？

    public ResultItem() {
        this.webPage = WebPage.emptyPage();
    }

    public static ResultItem custom() {
        ResultItem resultItem = new ResultItem();
        resultItem.setProcessTime(LocalDateTime.now());
        resultItem.setPersistSuccess(true);
        return resultItem;
    }

    public ResultItem(WebPage webPage) {
        this.webPage = webPage;
    }

    public ResultItem setResultFields(Collection resultFields) {
        if (resultFields == null) {
            this.resultFields = (Collection) Collections.EMPTY_MAP;
        }
        this.resultFields = resultFields;
        return this;
    }


}