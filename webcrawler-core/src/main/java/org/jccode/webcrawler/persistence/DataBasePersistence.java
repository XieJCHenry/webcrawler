package org.jccode.webcrawler.persistence;

import org.jccode.webcrawler.model.ResultItem;
import org.jccode.webcrawler.model.WebPage;

import java.util.List;

/**
 * DataBasePersistence
 * <p>
 * 使用druid的数据库连接池
 *
 * @Description TODO 暂不提供从配置文件中读取的功能
 * @Author jc-henry
 * @Date 2019/12/6 16:13
 * @Version 1.0
 **/
public class DataBasePersistence extends AbstractPersistence implements Runnable {

    private final String URL;
    private final String DRIVER;
    private final String USERNAME;
    private final String PASSWORD;


    public DataBasePersistence(String url, String driver, String username,
                               String password) {
        this.URL = url;
        this.DRIVER = driver;
        this.USERNAME = username;
        this.PASSWORD = password;
    }

    @Override
    public void run() {

    }


    @Override
    public void process(ResultItem resultItem) {

    }
}
