package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jccode.webcrawler.conts.HttpConstant;
import org.jccode.webcrawler.conts.SystemConstants;
import org.jccode.webcrawler.model.ResultItem;
import org.slf4j.Logger;
import org.jccode.webcrawler.model.WebPage;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/**
 * FilePersistence
 * <p>
 *
 * @Description 写入本地文件  TODO 1、是否有必要开启线程？ 2、增加写入到excel文件功能
 * @Author jc-henry
 * @Date 2019/12/5 20:12
 * @Version 1.0
 **/
@Getter
public class FilePersistence extends AbstractPersistence  {

    private final Logger logger = LoggerFactory.getLogger(FilePersistence.class);

    private static String DEFAULT_SUFFIX = ".dat";

    private static String DEFAULT_FOLDER = "output";

    private static final String DEFAULT_PATH = SystemConstants.DEFAULT_PATH;

    private static final String LOCAL_ENCODING = SystemConstants.LOCAL_ENCODING;

    private static final String LOCAL_SEPARATOR = SystemConstants.LOCAL_SEPARATOR;

    @Setter
    private String path;

    @Setter
    private String suffix;

    @Setter
    private String name;


    public FilePersistence() {
        this.suffix = DEFAULT_SUFFIX;
        this.path = DEFAULT_PATH + LOCAL_SEPARATOR + DEFAULT_FOLDER;
    }

    public FilePersistence(String path) {
        this(path, null);
    }

    public FilePersistence(String path, String suffix) {
        this(path, null, suffix);
    }

    public FilePersistence(String path, String name, String suffix) {
        this.path = path;
        this.name = name;
        this.suffix = suffix;
    }


    /**
     * 分三种情况处理：文本文件，二进制数据流，未能探明类型的文件
     *
     * @param
     */
//    @Override
    public void process(ResultItem resultItem) {
        WebPage webPage = resultItem.getWebPage();
        if (webPage.getContentType().equals(HttpConstant.ContentType.UNKNOWN)) {
            storageBinaryData(webPage, generateFilePath(path,
                    UUID.randomUUID().toString(), DEFAULT_SUFFIX));
        } else if (webPage.isBinary()) {
            if (this.name == null) {
                this.name = UUID.randomUUID().toString();
            }
            storageBinaryData(webPage, generateFilePath(path, name,
                    extractSuffix(webPage)));
        } else {
            storageTextFile(webPage, generateFilePath(path, webPage.getTitle(),
                    extractSuffix(webPage)));
        }
    }

    /**
     * 存储文本文件
     *
     * @param webPage
     */
    private void storageTextFile(WebPage webPage, String storagePath) {
        String charSet = webPage.getCharSet() != null ? webPage.getCharSet() :
                LOCAL_ENCODING;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(storagePath)),
                        charSet))) {
            writer.write(webPage.getRawText());
            writer.flush();
            logger.info("Storage Text File success: {}", storagePath);
        } catch (IOException e) {
            logger.error("Exception occurred while storage file : {}", storagePath, e);
//            e.printStackTrace();
        }
    }

    /**
     * 存储二进制文件
     * <p>
     * 二进制文件的文件名由用户指定，用户需要根据抓取内容自定义文件名的抓取方式，
     * 如果没有定义文件名，将使用UUID。
     *
     * @param webPage
     */
    private void storageBinaryData(WebPage webPage, String storagePath) {
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(new File(storagePath)))) {
            bos.write(webPage.getBytes());
            bos.flush();
            logger.info("Storage Binary File success: {}", storagePath);
        } catch (FileNotFoundException e) {
            logger.error("Storage Path not exists: {}", storagePath);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Exception occurred while storage file : {}", storagePath, e);
//            e.printStackTrace();
        }
    }

    /**
     * 从URL路径或者Content-Type提取后缀名
     * <p>
     * 如果都无法提取，则默认为“.dat”
     *
     * @param webPage
     * @return
     */
    private String extractSuffix(WebPage webPage) {
        if (this.suffix != null) {
            return this.suffix;
        }
        String suffix = DEFAULT_SUFFIX;
        if (!webPage.isBinary()) {
            String var1 = webPage.getContentType();
            if (StringUtils.isNotBlank(var1) && var1.contains("/")) {
                suffix = var1.split("/")[1];
            }
        } else {
            String var1 = webPage.getPath();
            int i = var1.lastIndexOf(".");
            if (i != -1) {
                suffix = var1.substring(i);
            }
        }
        this.suffix = suffix;
        return suffix;
    }

    private String generateFilePath(String path, String fileName, String suffix) {
        StringBuilder appender = new StringBuilder();
        appender.append(path.endsWith(LOCAL_SEPARATOR) ? (path + fileName) :
                (path + LOCAL_SEPARATOR + fileName));
        appender.append(suffix.startsWith(".") ? suffix : ("." + suffix));
        return appender.toString();
    }


}
