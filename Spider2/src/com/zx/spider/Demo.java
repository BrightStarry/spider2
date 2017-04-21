package com.zx.spider;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Administrator on 2017-04-21.
 */
public class Demo {

    private static final String  URL = "https://www.javbus.co/MCSR-249";
    private static final String  GET_URL = "https://www.javbus.co/ajax/search-modal.php";
    private static final String  POST_URL = "https://www.javbus.co/ajax/uncledatoolsbyajax.php";
    public static void main(String [] args) throws IOException {
        test();
    }
    public static void test() throws IOException {
       HttpClientUtil httpClientUtil = new HttpClientUtil();
        CloseableHttpClient sslHttpClient = httpClientUtil.getSSLHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        httpClientUtil.setDefaultHeaders(httpGet);
        CloseableHttpResponse response = sslHttpClient.execute(httpGet);
        String html = EntityUtils.toString(response.getEntity());
        HttpClientUtil.closeHttpResponse(response);
        Document doc = Jsoup.parse(html);
        Element script= doc.getElementsByTag("script").get(8);
        String[] strs = script.data().split("var");
        String gid = null;
        String uc = null;
        String img = null;
        String floor = String.valueOf((int)(Math.random() * 1000));
        String lang = "zh";
        for(String temp : strs){
            if(temp.contains("=")){
                if(temp.contains("gid")){
                    gid = temp.substring(temp.indexOf("=")+1,temp.length()-1).trim();
                    gid = gid.substring(0,gid.length()-1);
                }

                if(temp.contains("uc")){
                    uc = temp.substring(temp.indexOf("=")+1,temp.length()-1).trim();
                    uc = uc.substring(0,uc.length()-1);
                }
                if(temp.contains("img")){
                    img = temp.substring(temp.indexOf("=")+1,temp.length()-1).trim();
                    img = img.substring(1,img.length() -1);
                    img = img.substring(0,img.length() -1);
                }
            }
        }
        System.out.println(gid);
        System.out.println(uc);
        System.out.println(img);
        System.out.println(floor);
        System.out.println(lang);




        String postUrl = POST_URL
                + "?gid=" + gid
                + "&lang=" + lang
                + "&img=" + img
                + "&uc=" + uc
                + "&floor=" + floor;
        System.out.println(postUrl);
        HttpPost post = new HttpPost(postUrl);
//        post.setHeader("Cache-Control","max-age=0");
//        post.setHeader("Accept","*/*");
//        post.setHeader("Accept-Encoding","gzip, deflate");
//        post.setHeader("Accept-Language","zh-CN,zh;q=0.8");
//        post.setHeader("X-Requested-With","XMLHttpRequest");
        post.setHeader("Referer",URL);
//        post.setHeader("Connection","keep-alive");
//        post.setHeader("Host","www.javbus.co");
        httpClientUtil.setDefaultHeaders(post);
        CloseableHttpResponse re = sslHttpClient.execute(post);
        System.out.println(EntityUtils.toString(re.getEntity()));


    }
}
