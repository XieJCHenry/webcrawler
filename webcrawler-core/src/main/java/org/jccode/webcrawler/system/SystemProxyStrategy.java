package org.jccode.webcrawler.system;

import org.jccode.webcrawler.model.ProxyModel;

/**
 * SystemProxyStrategy
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/7 21:40
 * @Version 1.0
 **/
public interface SystemProxyStrategy {

    /**
     * 返回系统代理
     *
     * @return
     */
    ProxyModel inspect();
}
