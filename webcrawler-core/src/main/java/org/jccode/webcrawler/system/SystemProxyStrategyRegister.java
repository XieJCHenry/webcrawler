package org.jccode.webcrawler.system;

import org.apache.commons.lang3.SystemUtils;

import java.util.Map;
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
    private  Map<String, SystemProxyStrategy> proxyStrategyMap;

    private static volatile SystemProxyStrategyRegister INSTANCE;

    private SystemProxyStrategyRegister() {
    }

    {
        INSTANCE.proxyStrategyMap = new ConcurrentHashMap<>(DEFAULT_SIZE);
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
        return proxyStrategyMap.get(osName.toUpperCase());
    }

    public SystemProxyStrategy system() {
        String osName = SystemUtils.OS_NAME.toUpperCase();
        return proxyStrategyMap.get(osName);
    }

    public int size() {
        return proxyStrategyMap.size();
    }


}
