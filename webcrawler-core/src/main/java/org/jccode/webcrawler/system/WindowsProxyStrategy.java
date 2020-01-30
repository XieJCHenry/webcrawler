package org.jccode.webcrawler.system;

import org.apache.commons.lang3.StringUtils;
import org.jccode.webcrawler.model.ProxyModel;
import org.jccode.webcrawler.util.ProxyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * WindowsProxyStrategy
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/7 21:44
 * @Version 1.0
 **/
public class WindowsProxyStrategy extends AbstractSystemProxyStrategy {

    private static final String WINDOWS_REGEDIT_PATH = "HKEY_CURRENT_USER\\Software" +
            "\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";

    private static final String WINDOWS_REG_QUERY_COMMAND =
            "reg query \"" + WINDOWS_REGEDIT_PATH + "\"";

    private static final String WINDOWS_PROXY_SERVER = "ProxyServer";

    public WindowsProxyStrategy() {
        super.setOsName("Windows");
    }


    @Override
    public ProxyModel inspect() {
        ProxyModel systemProxy = getSystemProxy();
        if (systemProxy != null) {
            systemProxy.setReliable(ProxyUtil.validateProxy(
                    systemProxy.getHost(), systemProxy.getPort()));
            return systemProxy;
        }
        return null;
    }

    private ProxyModel getSystemProxy() {
        try {
            String localEncoding = System.getProperty("file.charset");
            Process process = Runtime.getRuntime().exec(WINDOWS_REG_QUERY_COMMAND);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), localEncoding));
            String line;
            while ((line = reader.readLine()) != null && !line.contains(WINDOWS_PROXY_SERVER))
                ;
            if (!StringUtils.isEmpty(line)) {
                return extractSystemProxyInfo(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ProxyModel extractSystemProxyInfo(String line) {
        char[] array = line.toCharArray();
        String var1 = null;
        String[] res;
        for (int i = 0; i < array.length; i++) {
            if (Character.isDigit(array[i])) {
                var1 = new String(array, i, array.length - i);
                break;
            }
        }
        if (var1 == null || var1.isEmpty()) {
            return null;
        } else {
            res = var1.split(":");
            return new ProxyModel(res[0], Integer.parseInt(res[1]));
        }
    }

}
