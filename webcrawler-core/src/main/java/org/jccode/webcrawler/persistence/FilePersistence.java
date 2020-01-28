package org.jccode.webcrawler.persistence;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.model.ResultItem;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * FilePersistence
 *
 * @Description 写入本地文件  TODO 1、是否有必要开启线程？ 2、增加写入到excel文件功能
 * @Author jc-henry
 * @Date 2019/12/5 20:12
 * @Version 1.0
 **/
@Getter
@Setter
public class FilePersistence extends AbstractPersistence {

    private final Logger log = Logger.getLogger(FilePersistence.class);

    private static String DEFAULT_SUFFIX = ".dat";

    private static String DEFAULT_FOLDER = "output";

    private static final String DEFAULT_PATH = System.getProperty("user.dir");

    private static final String LOCAL_ENCODING = System.getProperty("file.encoding");

    private static final String LOCAL_SEPARATOR = System.getProperty("file.separator");

    private String path;

    private String suffix;


    public FilePersistence() {
        this.suffix = DEFAULT_SUFFIX;
        this.path = DEFAULT_PATH + LOCAL_SEPARATOR + DEFAULT_FOLDER;
    }

    public FilePersistence(String path) {
        this(path, null);
    }

    public FilePersistence(String path, String suffix) {
        this.path = path;
        this.suffix = suffix == null ? DEFAULT_SUFFIX : suffix;
    }


    @Override
    public void process(List<ResultItem> resultItems) {
        for (ResultItem item : resultItems) {
            process(item);
        }
    }

    //    @Override
    protected void process(ResultItem resultItem) {
        if (resultItem == null) {
            throw new NullPointerException("ResultItem isn't initialized");
        }
        // 默认路径为当前工程的路径
//        if (Strings.isNullOrEmpty(path)) {
//            throw new PersistencePathUnValidException();
//        }
        String storagePath = generateFilePath(path, resultItem.getItemName(), suffix);
        File target = new File(storagePath);

        try {
            if (!target.exists() || target.isDirectory() || target.mkdirs()) {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(target),
                                LOCAL_ENCODING));
                writer.write(resultItem.getContext());
                resultItem.setPersistenceTime(LocalDateTime.now());
                resultItem.setConserved(true);
                log.info("Success to storage content : " + resultItem.getItemName() +
                        "[" + resultItem.getPersistenceTime() + "]");
            } else {
                resultItem.setConserved(false);
                log.warn("Failed to storage content : " + resultItem.getItemName());
            }
        } catch (IOException e) {
            log.error("Exception occur during storage content : {}", e);
        }
    }

    private String generateFilePath(String path, String fileName, String suffix) {
        StringBuilder appender = new StringBuilder();
        appender.append(path.endsWith(LOCAL_SEPARATOR) ? (path + fileName) :
                (path + LOCAL_SEPARATOR + fileName));
        appender.append(suffix.startsWith(".") ? suffix : ("." + suffix));
        return appender.toString();
    }


}
