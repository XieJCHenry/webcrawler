package org.jccode.webcrawler.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * WebPage
 *
 * @Description 存储下载的网页，如果是text，则包含html；如果是二进制数据，则包含二进制字节
 * @Author jc-henry
 * @Date 2020/1/27 9:28
 * @Version 1.0
 **/
@Data
@ToString(exclude = {"bytes", "content", "rawText"})
@EqualsAndHashCode(exclude = {"content","rawText","bytes"})
public class WebPage {

    private String site;
    private String path;
    private int status;
    private long contentLength;
    private String title;
    private String content;
    private String rawText;
    private String charSet;
    private String contentType;
    private LocalDateTime time;
    private boolean downloadSuccess;
    private boolean isBinary;
    /**
     * 存储二进制数据？
     */
    private byte[] bytes;

    /**
     * LinkedHashMap
     */
    private Map<String, String> headers;

    public WebPage() {
    }

    public static WebPage fail() {
        WebPage page = new WebPage();
        page.setDownloadSuccess(false);
        page.setTime(LocalDateTime.now());
        return page;
    }

    public static EmptyWebPage emptyPage() {
        return new EmptyWebPage();
    }

    @Getter
    static class EmptyWebPage extends WebPage {
        private final String site = null;
        private final String content = null;
        private final int status = -1;
        private final long contentLength = -1;
        private final boolean isBinary = true;
        private final boolean downloadSuccess = false;
        private final LocalDateTime time;

        EmptyWebPage() {
            this.time = LocalDateTime.now();
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return time.hashCode();
        }
    }
}
