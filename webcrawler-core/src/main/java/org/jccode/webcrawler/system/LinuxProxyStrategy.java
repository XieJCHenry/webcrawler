package org.jccode.webcrawler.system;

import org.jccode.webcrawler.model.ProxyModel;

/**
 * LinuxProxyStrategy
 *
 * TODO 暂时不实现
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/7 22:03
 * @Version 1.0
 **/
public class LinuxProxyStrategy extends AbstractSystemProxyStrategy {

    public LinuxProxyStrategy() {
        super.setOsName("Linux");
    }


    @Override
    public ProxyModel inspect() {
        String localEncoding = System.getProperty("file.encoding");

        return null;
    }
}
