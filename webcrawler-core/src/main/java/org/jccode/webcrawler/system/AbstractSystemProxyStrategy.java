package org.jccode.webcrawler.system;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.jccode.webcrawler.model.ProxyModel;

/**
 * AbstractSystemProxyStrategy
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/8 13:28
 * @Version 1.0
 **/
public abstract class AbstractSystemProxyStrategy implements SystemProxyStrategy {

    protected String osName;

//    @Override
//    public abstract ProxyModel inspect();


    public final void setOsName(String osName) {
        Asserts.notEmpty(osName, "OsName");
        this.osName = osName.toUpperCase();
    }

    public String getOsName() {
        return osName;
    }
}
