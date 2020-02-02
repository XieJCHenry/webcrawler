package org.jccode.webcrawler.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * CollectionUtils
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/2 12:25
 * @Version 1.0
 **/
public class CollectionUtils {
    private CollectionUtils() {
    }

    public static <T> void removeDuplicate(Collection<T> collection) {
        Set<T> helperSet = new LinkedHashSet<>(collection.size());
        helperSet.addAll(collection);
        collection.clear();
        collection.addAll(helperSet);
        helperSet.clear();
    }
}
