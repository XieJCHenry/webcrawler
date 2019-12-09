package org.jccode.webcrawler.http;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.conts.HttpResponseCode;

import java.io.IOException;

/**
 * CustomResponseHandler
 * <p>
 * HttpClient的响应处理器
 *
 * @Author jc-henry
 * @Date 2019/12/8 17:17
 * @Version 1.0
 **/
public class CustomResponseHandler implements ResponseHandler {
    private final Logger log = Logger.getLogger(CustomResponseHandler.class);

    @Override
    public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        int code = httpResponse.getStatusLine().getStatusCode();
        if (code == HttpResponseCode.OK.code) {
            HttpEntity entity;
            return (entity = httpResponse.getEntity()) == null ? null :
                    EntityUtils.toString(entity);
        } else {
            log.warn("Unexpected response code: " + code);
        }
        return null;
    }
}
