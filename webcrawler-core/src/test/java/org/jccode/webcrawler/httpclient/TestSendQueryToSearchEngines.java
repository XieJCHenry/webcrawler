package org.jccode.webcrawler.httpclient;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import sun.security.ssl.SSLSocketFactoryImpl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * TestSendQueryToSearchEngines
 * <p>
 * httpclient发送请求测试
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/1 15:02
 * @Version 1.0
 **/
public class TestSendQueryToSearchEngines {

    private static Logger log = Logger.getLogger(TestSendQueryToSearchEngines.class);

    private CloseableHttpClient client;

    /**
     * https://juejin.im/
     * <p>
     * success
     */
    @Test
    public void sendRequestToJuejinHomePage() {
        String url = "https://juejin.im/";
        client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        try (CloseableHttpResponse response = client.execute(get)) {
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                System.out.println(header.toString());
            }
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * https://web-api.juejin.im/query
     * <p>
     * success
     * <p>
     */
    @Test
    public void sendRequestToJuejinContent() {
        String url = "https://web-api.juejin.im/query";
        client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        Header[] headers = new Header[]{
//                new BasicHeader("Host", "web-api.juejin.im"),
//                new BasicHeader("Origin", "https://juejin.im"),
                new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; " +
                        "x64)" +
                        " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
                        "Safari/537.36"),
                new BasicHeader("Content-Type", "application/json"),
                new BasicHeader("X-Agent", "Juejin/Web"),   // 必需，其他均为非必需
//                new BasicHeader("DNT", "1"),
//                new BasicHeader("Accept", "*/*"),
//                new BasicHeader("Sec-Fetch-Site", "same-site"),
//                new BasicHeader("Sec-Fetch-Mode", "cors"),
//                new BasicHeader("Referer", "https://juejin.im/"),
//                new BasicHeader("Accept-Encoding", "gzip, deflate, br"),
//                new BasicHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0
//                .7")
        };
        post.setHeaders(headers);
        // query id 是未登录状态下的id
        String requestEntity = "{\"operationName\":\"\",\"query\":\"\"," +
                "\"variables\":{\"first\":20,\"after\":\"\",\"order\":\"POPULAR\"}," +
                "\"extensions\":{\"query\":{\"id\":\"21207e9ddb1de777adeaca7a2fb38030" +
                "\"}}}";
        StringEntity entity = new StringEntity(requestEntity,
                ContentType.APPLICATION_JSON);
        post.setEntity(entity);

        try (CloseableHttpResponse response = client.execute(post)) {
            System.out.println("---------> " + response.getStatusLine());
            HttpEntity responseEntity = response.getEntity();
            String contentType = responseEntity.getContentType().getValue();
            System.out.println("contentType : " + contentType);
            if (contentType.equals(ContentType.APPLICATION_JSON.getMimeType())) {
                InputStream input = responseEntity.getContent();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(new BufferedInputStream(input)));
                PrintWriter writer = new PrintWriter(System.out);
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            }
            EntityUtils.consume(responseEntity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /****************************************************************************/


    @Test
    public void testSendRequestToBaidu() {
        String url = "https://www.baidu.com/";
        client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        get.setHeaders(defaultHeaders());
        get.setHeader("Host", "www.baidu.com");

        try (CloseableHttpResponse response = client.execute(get)) {
            Header[] headers = response.getAllHeaders();
            printHeaders(headers);
            HttpEntity entity = response.getEntity();
            printEntityInfo(entity);
            System.out.println("**************************************");
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用百度查询的步骤：
     * 1、向https://www.baidu.com/ 发送get请求
     * 2、将response中的Set-Cookie全部取出，放到下一次get请求中
     * 3、在下一次请求中，设置Header: Referer和Host
     * <p>
     * <p>
     * https://www.baidu.com/s?wd=xxx
     */
    @Test
    public void testBaiduQuerying() {
        String keyWord = "计算机网络";
        String url = "https://www.baidu.com/s?wd=";
        try {
            String ecKeyWord = URLEncoder.encode(keyWord, "UTF-8");
            ecKeyWord = ecKeyWord.toUpperCase();   // 这一步可有可无
            url += ecKeyWord;
            System.out.println(url);
            client = HttpClientBuilder.create()
//                    .setUserAgent(HttpConst.USER_AGENT)
                    .build();
            HttpGet get = new HttpGet(url);
            Header[] headers = new Header[]{
                    new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64;" +
                            " x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0" +
                            ".3904.108 Safari/537.36"),
                    new BasicHeader("Host", "www.baidu.com"),
                    new BasicHeader("Accept-Encoding", "gzip, deflate"),
                    new BasicHeader("Accept", "text/html,application/xhtml+xml," +
                            "application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8," +
                            "application/signed-exchange;v=b3"),
                    new BasicHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;" +
                            "q=0.7"),
//                    new BasicHeader("Sec-Fetch-Mode","navigate"),
                    new BasicHeader("Referer", "https://www.baidu.com"),
                    new BasicHeader("is-Referer", "https://www.baidu.com"),
                    // Cookie 为必需
                    new BasicHeader("Cookie", "BAIDUID=15FBF73C8DEDB32BCA910DB49EA5FB68" +
                            ":FG=1; BIDUPSID=15FBF73C8DEDB32BCA910DB49EA5FB68; " +
                            "PSTM=1553614290; delPer=0; BD_HOME=0; " +
                            "H_PS_PSSID=1435_21088_30211_20883_30087_30071; " +
                            "BD_UPN=13314752; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; " +
                            "BD_CK_SAM=1; PSINO=6; " +
                            "H_PS_645EC=456dwnZu" +
                            "%2BtUBWd1gxHQbtlanwpk2PSoJChn77LnTD7nuuANjMYz0RkuNsXM; " +
                            "COOKIE_SESSION" +
                            "=0_0_1_0_0_1_0_0_0_1_0_0_0_0_0_0_0_0_1575265052%7C1" +
                            "%230_0_1575265052%7C1"),
                    new BasicHeader("Connection", "Keep-Alive")
            };
            get.setHeaders(headers);
            try (CloseableHttpResponse response = client.execute(get)) {
                System.out.println("state : " + response.getStatusLine());
                System.out.println("*********************************************");
                Header[] responseAllHeaders = response.getAllHeaders();
                for (Header header : responseAllHeaders) {
                    System.out.println(header.toString());
                }
                HttpEntity entity = response.getEntity();
                printEntityInfo(entity);
                System.out.println(EntityUtils.toString(entity));

                EntityUtils.consume(entity);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用百度查询的步骤：
     * 1、向https://www.baidu.com/ 发送get请求
     * 2、将response中的Set-Cookie全部取出，放到下一次get请求中
     * 3、在下一次请求中，设置Header: Referer和Host
     * <p>
     * 测试通过！
     * <p>
     * 百度搜索API：https://www.cnblogs.com/sunsky303/p/11045113.html
     * <p>
     * TODO 如何管理Cookie？
     * <p>
     * https://www.baidu.com/s?wd=xxx
     */
    @Test
    public void testBaiduQuerying2() throws UnsupportedEncodingException,
            URISyntaxException {
        String keyWord = "计算机网络";
        String queryUrl = "https://www.baidu.com/s?wd=" + URLEncoder.encode(keyWord,
                "UTF-8");
        // 使用URIBuilder构建复杂URI
//        URI queryUri = new URIBuilder()
//                .setPath("/s")
//                .setParameter("wd", keyWord)
//                .build();

        // 第一步：获得Cookie
        String baseUrl = "https://www.baidu.com/";
        HttpGet get = new HttpGet(baseUrl);
        get.setHeaders(defaultHeaders());
        get.setHeader("Host", "www.baidu.com");
        client = HttpClients.createDefault();

        HttpGet queryGet = new HttpGet(queryUrl);
        queryGet.setHeaders(defaultHeaders());

        StringBuilder cookieHeader = new StringBuilder();
        // 如果只需要读取响应的一小部分内容，这时候不应该调用EntityUtil.consume()
        // 而是直接response.close()。EntityUtils.consume()将消耗所有实体，这时
        // 如果只需要读取一小部分内容，消耗实体带来的开销比直接response.close()要高。
        try (CloseableHttpResponse firstResponse = client.execute(get)) {
            for (Header header : firstResponse.getAllHeaders()) {
                if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                    String var1 = header.getValue();
                    String cookie = var1.substring(0, var1.indexOf(";") + 1);
                    cookieHeader.append(cookie + " ");
                }
            }
            // 官方推荐：使用HeaderIterator迭代Header[]，通过传入参数指明要迭代的Header
//            HeaderIterator headerIterator = firstResponse.headerIterator("Set-Cookie");
//            while (headerIterator.hasNext()) {
//                Header header = headerIterator.nextHeader();
//                String var1 = header.getValue();
//                String cookie = var1.substring(0, var1.indexOf(";") + 1);
//                cookieHeader.append(cookie + " ");
//            }

//            System.out.println("Cookie : " + cookieHeader.toString());
            // 将Cookie填充到下一次查询中
            queryGet.setHeader("Cookie", cookieHeader.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 第三步，开始下一次查询
        queryGet.setHeader("Host", "www.baidu.com");
        queryGet.setHeader("Referer", "https://www.baidu.com");
        queryGet.setHeader("is-Referer", "https://www.baidu.com");
        try (CloseableHttpResponse response = client.execute(queryGet)) {
            Header[] headers = response.getAllHeaders();
            printHeaders(headers);
            HttpEntity entity = response.getEntity();
            // 关于EntityUtils，官方建议：
            // 当且仅当 HttpEntity来源于可信任的Http服务器以及长度已知时，
            // 才使用EntityUtils.toString()，其他时候请使用
            // entity.getContent()获得java.io.inputStream
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /****************************************************************************/

    /**
     * https://cn.bing.com/?FORM=BEHPTB 国内版
     * https://cn.bing.com/?FORM=BEHPTB&ensearch=1 国际版
     */
    @Test
    public void testSendRequestToBing() {
        String url = "https://cn.bing.com/?FORM=BEHPTB";
//        url="https://cn.bing.com/?FORM=BEHPTB&ensearch=1";
        client = HttpClients.createDefault();

        HttpGet get = new HttpGet(url);
        get.setHeaders(defaultHeaders());
        get.setHeader("Host", "cn.bing.com");

        try (CloseableHttpResponse response = client.execute(get)) {
            printHeaders(response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 国际版：https://cn.bing.com/?FORM=BEHPTB&ensearch=1
     * <p>
     * 测试通过
     *
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testBingQuerying() throws URISyntaxException,
            UnsupportedEncodingException {
        String baseUrl = "https://cn.bing.com/?FORM=BEHPTB&ensearch=1";
        client = HttpClients.createDefault();

        HttpGet get = new HttpGet(baseUrl);
        get.setHeaders(defaultHeaders());
        get.setHeader("Host", "cn.bing.com");

        String keyWord = "数据结构";
        URI queryUri = new URIBuilder("https://cn.bing.com").setPath("/search")
                .setParameter("q", keyWord)
                .build();
        HttpGet queryGet = new HttpGet(queryUri);
        StringBuilder cookieAppender = new StringBuilder();
        try (CloseableHttpResponse response = client.execute(get)) {
            HeaderIterator iterator = response.headerIterator("Set-Cookie");
            while (iterator.hasNext()) {
                String var1 = iterator.nextHeader().getValue();
                String cookie = var1.substring(0, var1.indexOf(";"));
                cookieAppender.append(cookie).append(" ");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        queryGet.setHeaders(defaultHeaders());
        queryGet.setHeader("Host", "cn.bing.com");
        queryGet.setHeader("Referer", baseUrl);
        queryGet.setHeader("Cookie", cookieAppender.toString());

        try (CloseableHttpResponse response = client.execute(queryGet)) {
            printHeaders(response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));

            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSendRequestToGoogle() {
        String url = "https://www.google.com";
        /*--设置本地代理--*/
        HttpHost proxy = new HttpHost("127.0.0.1", 1080);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        /*--------------*/
        client = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
                        "Safari/537.36")
                .setDefaultHeaders(clientDefaultHeaders())
                .build();
        HttpGet get = new HttpGet(url);
//        get.setHeaders(defaultHeaders());
        get.setHeader("Host", "www.google.com");

        try (CloseableHttpResponse response = client.execute(get)) {
            System.out.println(response.getStatusLine());
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("status code is " + response.getStatusLine().getStatusCode());
            }
            Header[] headers = response.getAllHeaders();
            printHeaders(headers);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果不携带Cookie,在响应头中会出现如下报文：
     * P3P: CP="This is not a P3P policy! See g.co/p3phelp for more info."
     * 如果在浏览器中禁用了所有Cookie，也会出现该报文。
     * <p>
     * 如果携带Cookie，响应头中依然会出现该报文！
     * <p>
     * 注意：默认情况下，HttpClients.custom().build()得到的HttpClient会在同一个会话中
     * 自动保存Cookie，因此无需手动添加Cookie
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testGoogleQuerying() throws UnsupportedEncodingException {

        /* 设置ShadowSocksR代理 */
        HttpHost proxy = new HttpHost("127.0.0.1", 1080);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        /*--------------*/
        client = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
                        "Safari/537.36")
                .setDefaultHeaders(clientDefaultHeaders())
                .build();
        String baseUrl = "https://www.google.com";
        HttpGet get = new HttpGet(baseUrl);
        get.setHeader("Host", "www.google.com");

        String key = "数据结构";
        String queryUrl = "https://www.google.com/search?q=" + URLEncoder.encode(key,
                "UTF-8");
        HttpGet queryGet = new HttpGet(queryUrl);


        try (CloseableHttpResponse response = client.execute(get)) {
//            appendCookiesToHttpRequest(response, queryGet);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CloseableHttpResponse response = client.execute(queryGet)) {
            printHeaders(response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置HttpClient的Cookie策略
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testGoogleQuerying3() throws UnsupportedEncodingException {
        /* 设置ShadowSocksR代理 */
        HttpHost proxy = new HttpHost("127.0.0.1", 1080);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        // 设置Cookie策略
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        client = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 " +
                        "Safari/537.36")
                .setDefaultHeaders(clientDefaultHeaders())
                .setDefaultRequestConfig(globalConfig)
                .build();

        String baseUrl = "https://www.google.com";
        HttpGet get = new HttpGet(baseUrl);
        get.setHeader("Host", "www.google.com");
//        get.setConfig(globalConfig);
        try (CloseableHttpResponse response = client.execute(get)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        String key = "数据结构";
        String queryUrl = "https://www.google.com/search?q=" +
                URLEncoder.encode(key, "UTF-8");
        HttpGet queryGet = new HttpGet(queryUrl);
        queryGet.setHeader("Referer", "https://www.google.com/");
        try (CloseableHttpResponse response = client.execute(queryGet)) {
            StatusLine line = response.getStatusLine();
            printHeaders(response.getAllHeaders());
            if (line.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                System.out.println(EntityUtils.toString(entity));
                EntityUtils.consume(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /****************************************************************************/

    /**
     * 查询系统本地SSL协议
     */
    @Test
    public void showSSLProtocol() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            SSLSocketFactoryImpl socketFactory =
                    (SSLSocketFactoryImpl) sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket();
            log.info("Support Protocol");
            String[] protocols = socket.getSupportedProtocols();
            for (String protocol : protocols) {
                System.out.println(protocol);
            }
            log.info("Enabled Protocol");
            String[] enabledProtocols = socket.getEnabledProtocols();
            for (String protocol : enabledProtocols) {
                System.out.println(protocol);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private BasicHeader[] defaultHeaders() {
        BasicHeader[] headers = new BasicHeader[]{
                new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; " +
                        "x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904" +
                        ".108 Safari/537.36"),
                new BasicHeader("Accept-Encoding", "gzip, deflate"),
                new BasicHeader("Accept", "text/html,application/xhtml+xml," +
                        "application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8," +
                        "application/signed-exchange;v=b3"),
                new BasicHeader("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"),
                new BasicHeader("DNT", "1"),
                new BasicHeader("Connection", "Keep-Alive"),
        };
        return headers;
    }

    private List<BasicHeader> clientDefaultHeaders() {
//        return new ArrayList<>(Arrays.asList(defaultHeaders()));
        return Arrays.asList(defaultHeaders());
    }

    private void appendCookiesToHttpRequest(HttpResponse response,
                                            HttpRequest request) {
        HeaderIterator iterator = response.headerIterator("Set-Cookie");
        StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            Header header = iterator.nextHeader();
            String var1 = header.getValue();
            builder.append(var1, 0, var1.indexOf(";")).append(" ");
        }
        request.addHeader("Cookie", builder.toString());
    }

    private void printEntityInfo(HttpEntity entity) {
        System.out.println("*********************************************");
        System.out.println("ContentType = " + entity.getContentType());
        System.out.println("ContentLength = " + entity.getContentLength());
        System.out.println("ContentEncoding = " + entity.getContentEncoding());
        System.out.println("*********************************************");
    }

    private void printHeaders(Header[] headers) {
        System.out.println("*********************************************");
        for (Header header : headers) {
            System.out.println(header.toString());
        }
        System.out.println("*********************************************");
    }
}
