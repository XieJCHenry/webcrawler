package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.jccode.webcrawler.model.WebPage;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/**
 * FilePersistence
 *
 * @Description 写入本地文件  TODO 1、是否有必要开启线程？ 2、增加写入到excel文件功能
 * @Author jc-henry
 * @Date 2019/12/5 20:12
 * @Version 1.0
 **/
@Getter
public class FilePersistence {

    private final Logger logger = LoggerFactory.getLogger(FilePersistence.class);

    private static String DEFAULT_SUFFIX = ".dat";

    private static String DEFAULT_FOLDER = "output";

//    private static String DEFAULT_NAME = UUID.randomUUID().toString();

    private static final String DEFAULT_PATH = System.getProperty("user.dir");

    private static final String LOCAL_ENCODING = System.getProperty("file.encoding");

    private static final String LOCAL_SEPARATOR = System.getProperty("file.separator");

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
     * TODO 1、获取完整的文件信息：包括文件名，后缀名，存放路径
     * 区分二进制数据和文本文件的下载方式：
     * 二进制数据：从url路径获得文件名和后缀名
     * 文本文件：根据title或者url路径获得文件名和后缀名
     *
     * @param webPage
     */
    public void process(WebPage webPage) {
        if (webPage.isBinary()) {
            storageBinaryData(webPage);
        } else {
            storageTextFile(webPage);
        }
    }

    /**
     * 存储文本文件
     *
     * @param webPage
     */
    private void storageTextFile(WebPage webPage) {
        String storagePath = generateFilePath(path, webPage.getTitle(),
                extractSuffix(webPage));
        File file = new File(storagePath);
        String charSet = webPage.getCharSet() != null ? webPage.getCharSet() :
                LOCAL_ENCODING;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), charSet))) {
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
     *
     * 二进制文件的文件名由用户指定，用户需要根据抓取内容自定义文件名的抓取方式，
     * 如果没有定义文件名，将使用UUID。
     *
     * @param webPage
     */
    private void storageBinaryData(WebPage webPage) {
        if (this.name == null) {
            this.name = UUID.randomUUID().toString();
        }
        String storagePath = generateFilePath(path, name, extractSuffix(webPage));
        File file = new File(storagePath);
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(file))) {
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
