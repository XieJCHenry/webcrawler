package org.jccode.webcrawler.sample;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jccode.webcrawler.conts.HttpConst;

import java.io.*;

/**
 * SimpleCrawler
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/11/30 19:29
 * @Version 1.0
 **/
public class SimpleCrawler {

    private static final Logger logger = Logger.getLogger(SimpleCrawler.class);


    public static void main(String[] args) {
        String url = "http://juejin.im/";
//        url = "https://www.baidu.com";
        CloseableHttpClient client = HttpClientBuilder.create()
                .setUserAgent(HttpConst.USER_AGENT)
                .build();
        System.out.println("************************************************");
        HttpGet get = new HttpGet(url);
        get.setHeaders(initHeader());
//        for (Header header : get.getAllHeaders()) {
//            System.out.println(header.getName() + " : " + header.getValue());
//        }

        try (CloseableHttpResponse response = client.execute(get)) {
            HttpEntity entity = response.getEntity();
            Header encoding = entity.getContentEncoding();
            long contentLength = entity.getContentLength();
            Header contentType = entity.getContentType();
            if (contentLength == 0) {
                logger.error("Return no content!");
                return;
            }

            System.out.println("encoding = " + (encoding == null ? null : encoding.toString()));
            System.out.println("contentLength = " + contentLength);
            System.out.println("contentType = " + (contentType==null ? null : contentType.toString()));
            System.out.println("************************************************");
            InputStream input = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
//            BufferedWriter writer = new BufferedWriter(new PrintWriter());
            EntityUtils.consume(entity);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Header[] initHeader(){
        BasicHeader[] headers = new BasicHeader[]{
                new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"),
                new BasicHeader("Accept-Encoding","gzip, deflate, br"),
                new BasicHeader("Accept-Language","en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"),
                new BasicHeader("Connection","keep-alive"),
                new BasicHeader("Cookie","ab={}; _ga=GA1.2.1637645414.1550132929; gr_user_id=3066614c-695f-4fe6-913e-c45d505d851f; _gid=GA1.2.1132336445.1574822076; Hm_lvt_93bbd335a208870aa1f296bcd6842e5e=1575023346,1575092572,1575099274,1575100359; gr_session_id_89669d96c88aefbc=eb86c0a8-a9bd-4b7a-949a-6cb89fed7a55; gr_session_id_89669d96c88aefbc_eb86c0a8-a9bd-4b7a-949a-6cb89fed7a55=true; QINGCLOUDELB=7526744c262201bf8ae89c7035a8ce8c9eb2c663a78c233d245e9356cc89386b|XeJXJ|XeJXI; Hm_lpvt_93bbd335a208870aa1f296bcd6842e5e=1575114530"),
                new BasicHeader("Host","juejin.im"),
                new BasicHeader("Referer","https://juejin.im/post/5a23bdd36fb9a045272568a6"),
                new BasicHeader("Sec-Fetch-Mode","navigate"),
                new BasicHeader("Sec-Fetch-Site","same-origin"),
                new BasicHeader("Sec-Fetch-User","?1"),
                new BasicHeader("Upgrade-Insecure-Requests","1"),
                new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                        " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"),
//                new BasicNameValuePair("","")
        };
        return headers;
    }

}
