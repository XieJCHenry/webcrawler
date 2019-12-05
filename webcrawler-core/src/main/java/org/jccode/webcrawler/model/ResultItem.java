package org.jccode.webcrawler.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
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

    private String charset;

    private String contentType;

    /**
     * 被执行保存的时间，TODO 使用定时任务批量保存
     */
    private long persistenceTime;

    /**
     * 生成时间
     */
    private long createTime;


    /**
     * 从输入里读取时就要确定charset，使用new String(byte[],charset)构造器
     */
    private String rawText;

    public ResultItem() {
        this.persistenceTime = System.currentTimeMillis();
        this.itemName = UUID.randomUUID().toString();
    }

    public String getUrl() {
        return url;
    }

    public ResultItem setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ResultItem setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public ResultItem setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public ResultItem setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getRawText() {
        return rawText;
    }

    public ResultItem setRawText(String rawText) {
        this.rawText = rawText;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public long getPersistenceTime() {
        return persistenceTime;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setPersistenceTime(long persistenceTime) {
        this.persistenceTime = persistenceTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


}
