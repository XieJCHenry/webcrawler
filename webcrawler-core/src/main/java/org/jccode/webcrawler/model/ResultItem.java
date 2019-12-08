package org.jccode.webcrawler.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * 持久化对象
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/11/30 19:17
 * @Version 1.0
 **/
@Getter
@EqualsAndHashCode
public class ResultItem {

    private String url;

    private String itemName;

    private int status;

    private String charSet;

    private String contentType;

    /**
     * 被执行保存的时间，TODO 使用定时任务批量保存
     */
//    private Date persistenceTime;
    private LocalDateTime persistenceTime;

    /**
     * 生成时间
     */
//    private Date createTime;
    private LocalDateTime createTime;

    /**
     * 与 persistenceTime 关联
     */
    private boolean isConserved;


    /**
     * 从输入里读取时就要确定charset，使用new String(byte[],charSet)构造器
     */
    private String rawText;

    public ResultItem() {
        this.createTime = LocalDateTime.now();
        this.itemName = UUID.randomUUID().toString();
        this.isConserved = false;
    }

    public ResultItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public ResultItem setStatus(int status) {
        this.status = status;
        return this;
    }

    public ResultItem setCharSet(String charSet) {
        this.charSet = charSet;
        return this;
    }

    public ResultItem setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ResultItem setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public ResultItem setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public ResultItem setPersistenceTime(LocalDateTime persistenceTime) {
        this.persistenceTime = persistenceTime;
        return this;
    }

    public boolean isConserved() {
        return isConserved;
    }

    public ResultItem setConserved(boolean conserved) {
        this.isConserved = conserved;
        return this;
    }

}
