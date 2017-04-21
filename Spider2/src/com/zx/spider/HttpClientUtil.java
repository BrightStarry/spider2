package com.zx.spider;

import com.sun.xml.internal.ws.transport.Headers;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * httpCLient 工具类
 * 原本准备设计成 属性和函数都是static的，但想了想应该是要多个线程共同使用的，就不那么弄了
 */
public class HttpClientUtil {
    //ssl
    private  CloseableHttpClient sslHttpClient;
    //普通
    private  CloseableHttpClient httpClient;
    //请求配置  是 HttpPost 和 HttpGet对象的
    private static RequestConfig requestConfig;
    //请求头
    private Map<String,String> header;



    static {
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();
    }
    //这个不是stiatc的原因是考虑到可能有多个不同header的httpClient
    {
        header = new HashMap<>();
//        header.put("Accept-Encoding","gzip, deflate");
//        header.put("Accept-Language","zh-CN,zh;q=0.8");
        header.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/50.0.2661.102 UBrowser/6.1.2107.204 Safari/537.36");
//        header.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        header.put("Connection","keep-alive");
//        header.put("Host","www.zhihu.com");
//        header.put("Referer","https://www.zhihu.com/");



    }

    /**
     *获取sslHttpClient 单例
     * 如果为空，可能会无限循环
     */
    public  CloseableHttpClient getSSLHttpClient() {
        if(this.sslHttpClient != null){
            return this.sslHttpClient;
        }
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有证书
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient sslHttpClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(sslFactory)
                    .setDefaultRequestConfig(HttpClientUtil.requestConfig)
                    .build();
            this.sslHttpClient = sslHttpClient;
            return this.sslHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getSSLHttpClient();
    }

    /**
     * 获取普通 httpClient
     */
    public  CloseableHttpClient getHttpClient(){
        if(httpClient != null){
            return this.httpClient;
        }
        this.httpClient = HttpClients
                .custom()
                .setDefaultRequestConfig(HttpClientUtil.requestConfig)
                .build();
        return this.httpClient;
    }

    /**
     * 设置默认请求头
     */
    public void setDefaultHeaders(HttpRequest request){
        for(Map.Entry<String,String> entry : header.entrySet()){
            request.setHeader(entry.getKey(),entry.getValue());
        }
    }

    /**
     * 关闭
     */
    public static void closeHttpClient(CloseableHttpClient httpClient){
        if(httpClient != null){
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                httpClient = null;
            }
        }
    }

    /**
     * 关闭 response
     */
    public static void closeHttpResponse(CloseableHttpResponse response){
        if(response != null){
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                response = null;
            }
        }
    }

    /**
     * 发起get请求，返回 response
     */
    public CloseableHttpResponse sendGetRequest(String url){
        HttpGet httpget = new HttpGet(url);
        setDefaultHeaders(httpget);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = getSSLHttpClient().execute(httpget);
            return httpResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 从response 中取出 html String
     * 如果没有访问成功，返回null
     */
    public String responseToString(CloseableHttpResponse response){
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            try {
                String html = EntityUtils.toString(response.getEntity(), "UTF-8");
                return html;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * response 下载文件到本地
     */
    public static boolean downloadFileToLocal(CloseableHttpResponse response,String path){

        InputStream in = null;
        OutputStream os = null;
        try {
            in = response.getEntity().getContent();
            os = new FileOutputStream(new File(path));
            byte[] buf =new byte[1024];
            int len;
            while((len = in.read(buf)) != -1){
                os.write(buf,0,len);

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }




}
