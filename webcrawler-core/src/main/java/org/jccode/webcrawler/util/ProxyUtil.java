package org.jccode.webcrawler.util;

import org.apache.log4j.Logger;
import org.jccode.webcrawler.model.ProxyModel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * ProxyUtil
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/1/29 22:53
 * @Version 1.0
 **/
public class ProxyUtil {

    private static final Logger logger = Logger.getLogger(ProxyUtil.class);

    private ProxyUtil() {
    }


    public static boolean validateProxy(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, 3000);
            return true;
        } catch (IOException e) {
            logger.warn("Proxy unavailable![Host = " + host + ", Port = " + port + "]");
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warn("Error occurred while closing socket of validate proxy"
                            , e);
                }
            }
        }
    }

}
