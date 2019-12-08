package org.jccode.webcrawler.persistence;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.exception.PersistencePathUnValidException;
import org.jccode.webcrawler.model.ResultItem;

import java.io.*;
import java.time.LocalDateTime;

/**
 * FilePersistence
 *
 * @Description 多线程写入本地文件
 * @Author jc-henry
 * @Date 2019/12/5 20:12
 * @Version 1.0
 **/
@Getter
@Setter
public class FilePersistence extends AbstractPersistence implements Runnable {

    private final Logger log = Logger.getLogger(FilePersistence.class);

    private static final String DEFAULT_SUFFIX = ".dat";

    private static final String LOCAL_ENCODING = System.getProperty("file.encoding");

    private static final String LOCAL_SEPARATOR = System.getProperty("file.separator");

    private String path;

    private String suffix;


    public FilePersistence() {
    }

    public FilePersistence(String path) {
        this(path, null);
    }

    public FilePersistence(ResultItem resultItem, String path, String suffix) {
        super(resultItem);
        this.path = path;
        this.suffix = suffix == null ? DEFAULT_SUFFIX : suffix;
    }


    public FilePersistence(String path, String suffix) {
        this.path = path;
        this.suffix = suffix == null ? DEFAULT_SUFFIX : suffix;
    }


    @Override
    public void run() {
        process();
    }

    @Override
    protected void process() {
        if (this.resultItem == null) {
            throw new NullPointerException("ResultItem isn't initialized");
        }
        if (Strings.isNullOrEmpty(path)) {
            throw new PersistencePathUnValidException();
        }
        String storagePath = generateFilePath(path, resultItem.getItemName(), suffix);
        File target = new File(storagePath);
        try {
            if (target.mkdirs()) {
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), LOCAL_ENCODING));
                writer.write(resultItem.getRawText());
                resultItem.setPersistenceTime(LocalDateTime.now());
                resultItem.setConserved(true);
                log.info("Success to storage content : " + resultItem.getItemName());
            } else {
                resultItem.setConserved(false);
                log.warn("Failed to storage content : " + resultItem.getItemName());
            }
        } catch (IOException e) {
            log.error("Exception occur during storage content : {}", e);
        }
    }

    private String generateFilePath(String path, String fileName, String suffix) {
        return path.endsWith(LOCAL_SEPARATOR) ?
                path + fileName + suffix :
                path + LOCAL_SEPARATOR + fileName + suffix;
    }


}
