package org.jccode.webcrawler.conts;

/**
 * SystemConstants
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/2 13:11
 * @Version 1.0
 **/
public class SystemConstants {
    private SystemConstants() {
    }

    public static final String LOCAL_ENCODING = System.getProperty("file.encoding");

    public static final String LOCAL_SEPARATOR = System.getProperty("file.separator");

    public static final String DEFAULT_PATH = System.getProperty("user.dir");
}
