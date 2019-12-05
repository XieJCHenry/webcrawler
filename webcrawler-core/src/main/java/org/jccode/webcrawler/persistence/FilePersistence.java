package org.jccode.webcrawler.persistence;

import com.google.common.base.Strings;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.exception.PersistencePathUnValidException;
import org.jccode.webcrawler.model.ResultItem;

import java.util.List;

/**
 * FilePersistence
 *
 * @Description 多线程写入本地文件
 * @Author jc-henry
 * @Date 2019/12/5 20:12
 * @Version 1.0
 **/
public class FilePersistence extends Thread implements Persistence {

    private final Logger log = Logger.getLogger(FilePersistence.class);

    private static final String DEFAULT_SUFFIX = ".dat";

    private String path;

    private ResultItem resultItem;

    private String suffix;


    public FilePersistence() {
    }


    public FilePersistence setPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            log.error("Persistence path is null or empty!");
            throw new PersistencePathUnValidException();
        }
        this.path = path;
        return this;
    }

    public FilePersistence setResultItem(ResultItem resultItem) {
        if (resultItem == null) {
            log.error("resultItem is null!");
            throw new NullPointerException("ResultItem is null!");
        }
        this.resultItem = resultItem;
        return this;
    }

    public FilePersistence setSuffix(String suffix) {
        if (Strings.isNullOrEmpty(suffix)) {
            log.info("use default suffix:" + DEFAULT_SUFFIX);
            this.suffix = DEFAULT_SUFFIX;
        }
        this.suffix = suffix;
        return this;
    }

    @Override
    public void run() {

    }

    @Override
    public void process(ResultItem resultItem) {

    }
}
