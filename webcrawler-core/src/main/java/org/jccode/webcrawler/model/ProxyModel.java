package org.jccode.webcrawler.model;

import lombok.Data;
import lombok.ToString;

/**
 * ProxyModel
 * <p>
 * 阶段一：完成windows下的代理获取以及检测
 * TODO 阶段二：尝试使用设计模式整合windows和linux的代理检测
 *
 * @Description
 * @Author jc-henry
 * @Date 2019/12/7 14:28
 * @Version 1.0
 **/
@Data
@ToString
public class ProxyModel {

    private String host;
    private int port;
    private String username;
    private String password;
    private Boolean reliable;


    /**
     * 本地代理
     *
     * @param host
     * @param port
     */
    public ProxyModel(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 远程代理
     * @param host
     * @param port
     * @param username
     * @param password
     */
    public ProxyModel(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
