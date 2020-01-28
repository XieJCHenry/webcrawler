package org.jccode.webcrawler.system;

import org.apache.commons.lang3.SystemUtils;
import sun.plugin2.util.SystemUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SystemProxyStrategyRegister
 * <p>
 * 系统代理策略注册，将不同操作系统的ProxyStrategy注册到该Register的map中。
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/8 12:59
 * @Version 1.0
 **/
public class SystemProxyStrategyRegister {

    /**
     * 主流操作系统无非就Windows、Linux、MacOS，因此不需要太大初始容量
     */
    private static final int DEFAULT_SIZE = 4;
    private static Map<String, SystemProxyStrategy> proxyStrategyMap;

    private static volatile SystemProxyStrategyRegister INSTANCE;

    private SystemProxyStrategyRegister() {
    }

    {
        proxyStrategyMap = new ConcurrentHashMap<>(DEFAULT_SIZE);
        SystemProxyStrategy windows = new WindowsProxyStrategy();
        SystemProxyStrategy linux = new LinuxProxyStrategy();
        proxyStrategyMap.put("WINDOWS", windows);
        proxyStrategyMap.put("LINUX", linux);
    }

    public static SystemProxyStrategyRegister getInstance() {
        if (INSTANCE == null) {
            synchronized (SystemProxyStrategyRegister.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SystemProxyStrategyRegister();
                }
            }
        }
        return INSTANCE;
    }


    public void put(String osName, SystemProxyStrategy strategy) {
        proxyStrategyMap.putIfAbsent(osName.toUpperCase(), strategy);
    }

    public SystemProxyStrategy get(String osName) {
        for (Map.Entry<String, SystemProxyStrategy> entry : proxyStrategyMap.entrySet()) {
            String s = entry.getKey();
            String var1, var2;
            boolean var3 = s.length() < osName.length();
            if (var3) {
                var1 = s;
                var2 = osName;
            } else {
                var1 = osName;
                var2 = s;
            }
            if (var2.contains(var1)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public SystemProxyStrategy system() {
        String osName = SystemUtils.OS_NAME.toUpperCase();
        return get(osName);
    }

    public int size() {
        return proxyStrategyMap.size();
    }


}
