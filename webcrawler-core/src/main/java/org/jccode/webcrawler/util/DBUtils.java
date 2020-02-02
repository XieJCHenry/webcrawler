package org.jccode.webcrawler.util;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DBUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/2 14:53
 * @Version 1.0
 **/
public class DBUtils {

    private static final Map<String, Connection> connectionsPool = new ConcurrentHashMap<>();

    private DBUtils(){}

}
