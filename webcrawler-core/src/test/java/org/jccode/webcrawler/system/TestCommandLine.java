package org.jccode.webcrawler.system;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * TestCommandLine
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/4 19:25
 * @Version 1.0
 **/
public class TestCommandLine {

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
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
//        String osName = properties.getProperty("os.name");
//        System.out.println(osName);
    }


    /**
     * 不同系统下获取系统进程PID的命令不同，因此要先获取os.Name，再执行对应命令。
     * <p>
     * TODO 是否能用设计模式去改造？对不同的操作系统执行不同的命令
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
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream(),
                        StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Test
    public void testQuerySystemRegedit() throws IOException {
        String path = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows" +
                "\\CurrentVersion\\Internet Settings";
        Process process = Runtime.getRuntime().exec("reg query \"" + path + "\"");
        String line;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        while ((line = reader.readLine()) != null) {
//            if (line.startsWith(path) && !line.equals(path)) {
//                System.out.println(line);
//            }
            System.out.println(line);
        }
    }

    @Test
    public void testQueryWindowsOSProxyRegValue() throws IOException {
        final String path = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows" +
                "\\CurrentVersion\\Internet Settings";
        Map<String, String> map = new LinkedHashMap<>();
        Process process = Runtime.getRuntime().exec("reg query \"" + path + "\"");
        String line;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        boolean isDefault = true;
        while ((line = reader.readLine()) != null) {
            if (line.contains("ProxyServer")) {
                System.out.println(line);
            }
        }
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            System.out.println(entry);
//        }
    }

    @Test
    public void testExtractSystemProxyInfo() {
        String regeditLine = "ProxyServer    REG_SZ    127.0.0.1:1080";
        char[] array = regeditLine.toCharArray();
        String var1 = null;
        String[] res;
        for (int i = 0; i < array.length; i++) {
            if (Character.isDigit(array[i])) {
                var1 = new String(array, i, array.length - i);
                break;
            }
        }
        res = var1.split(":");
        System.out.println(Arrays.toString(res));
    }

    @Test
    public void testApacheCommonUtils() {
        String os = SystemUtils.OS_NAME;
        System.out.println(os);
        System.out.println(SystemUtils.OS_VERSION);
        System.out.println(SystemUtils.IS_OS_LINUX);
        System.out.println(SystemUtils.IS_OS_MAC);
    }


    @Test
    public void testSystemProperties() {
        Properties properties = System.getProperties();
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }


}
