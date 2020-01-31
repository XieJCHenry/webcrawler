package org.jccode.webcrawler.util;

import org.apache.commons.lang3.StringUtils;
import org.jccode.webcrawler.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * UrlUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/29 20:48
 * @Version 1.0
 **/
public class UrlUtils {
    private UrlUtils() {
    }

    public static List<Task> convertToTasks(List<String> urls) {
        List<Task> taskList = new ArrayList<>(urls.size());
        for (String url : urls) {
            taskList.add(new Task(url));
        }
        return Collections.unmodifiableList(taskList);
    }

    /**
     * @param url "https://www.baidu.com/search?q=apache"
     * @return such as "https://www.baidu.com/"
     */
    public static String extractHost(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        int start = url.indexOf("://");
        if (start != -1) {
            int end;
            char[] values = url.toCharArray();
            for (end = start + 3; end < values.length && values[end] != '/'; end++) ;
            return url.substring(0, end);
        } else {
            return url.substring(0, url.indexOf("/"));
        }
    }

    /**
     * @param host
     * @return
     */
    public static String removeProtocol(String host) {
        int i = host.indexOf("://");
        return (i == -1) ? host : host.substring(i + 3);
    }

//    public static String fixIllegalCharacters(String intput ){
//
//    }
}
