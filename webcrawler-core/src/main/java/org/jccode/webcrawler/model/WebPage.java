package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WebPage
 *
 * @Description 存储下载的网页，如果是text，则包含html；如果是二进制数据，则包含二进制字节
 * @Author jc-henry
 * @Date 2020/1/27 9:28
 * @Version 1.0
 **/
@Data
@ToString(exclude = {"bytes","content","rawText"})
@EqualsAndHashCode
public class WebPage {

    // url = site + "/" + path;

    private String site;
    private String path = "";
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

    private Map<String, List<String>> headers = new HashMap<>();

    public WebPage() {
    }

    public static WebPage fail(){
        WebPage page = new WebPage();
        page.setDownloadSuccess(false);
        page.setTime(LocalDateTime.now());
        return page;
    }
}
