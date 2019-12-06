package org.jccode.webcrawler.system;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * TestDemo
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/4 19:25
 * @Version 1.0
 **/
public class TestDemo {

    @Test
    public void testParseHostAddress() throws UnknownHostException {
        String url = "cn.bing.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.google.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.baidu.com";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));

        url = "www.wikipedia.org";
        System.out.println(Arrays.toString(InetAddress.getAllByName(url)));
    }

    @Test
    public void testFetchCurrentProcessPID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        System.out.println(runtimeMXBean.getBootClassPath());
    }

    @Test
    public void testFetchAllJavaProcessesPID() throws IOException {
        Process process = Runtime.getRuntime().exec("jps");
        String line;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * 查看当前系统是windows还是linux
     */
    @Test
    public void testFetchSystemProperties() {
        Properties properties = System.getProperties();
        // 获取系统所有信息
//        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
//        for (Map.Entry<Object, Object> entry : entries) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
        String osName = properties.getProperty("os.name");
        System.out.println(osName);
    }


    /**
     * 不同系统下获取系统进程PID的命令不同，因此要先获取os.Name，再执行对应命令。
     * <p>
     * TODO 是否能用设计模式去改造呢？对不同的操作系统执行不同的命令
     */
    @Test
    public void testFetchAllSystemProcessesPID() throws IOException {
        final String cmd_windows = "tasklist";    // windows 查看系统进程和PID
        final String cmd_linux = "ps -ef";    // linux 查看系统进程和PID
        final String os_windows = "WINDOWS";
        final String os_linux = "LINUX";
        Properties properties = System.getProperties();
        String osName = properties.getProperty("os.name");
        if (osName == null)
            return;
        String command = osName.toUpperCase().contains(os_windows) ?
                cmd_windows : cmd_linux;
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }


    }


}
