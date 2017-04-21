package com.zx.a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *使用代理服务器抓取网页 
 */
public class Demo2 {
	//创建HttpClient对象
	private static HttpClient httpClient = HttpClientBuilder.create().build();
	
	
	/**
	 * 下载网页的代码
	 */
	public static boolean downloadPage(){
		InputStream in = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		
		//创建HttpPost
		HttpPost httpPost = new HttpPost("https://www.javbus.co/");
		//设置代理服务器
		//setProxy(null, httpPost);
		//设置方法参数
		//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//nameValuePairs.add(new BasicNameValuePair("a","a"));
		
		
		try{
			//执行方法
			HttpResponse response = httpClient.execute(httpPost);
			//获取HTTP状态码
			int statusCode = response.getStatusLine().getStatusCode();
			//如果返回200，表示成功，进行处理，其他状态码统一返回false
			if(statusCode == 200){
				System.out.println("成功访问");
				//获取到返回的实体 中的内容，也就是一个输入流
				in = response.getEntity().getContent();
				//用缓冲字符输入流包装
				br = new BufferedReader(new InputStreamReader(in));
				//给缓冲字符输出流
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/桌面/Desktop/101.txt")));
				
				String temp = null;
				while((temp = br.readLine()) != null){
					System.out.println("读取中。。。");
					bw.write(temp);
					bw.newLine();
				}
				System.out.println("读取完成");
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}	
	
	
	
	
	
	
	
	/**
	 *设置代理服务器的方法 
	 */
	public static void setProxy(HttpGet httpGet,HttpPost httpPost){
		//创建HttpHost类，并传入 代理iP 和 端口号
		HttpHost proxy = new HttpHost("49.79.59.43", 8088);
		//创建请求配置类  并把httpHost对象作为参数传入
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		//根据传过来的是哪个对象，设置代理参数
		if(httpGet != null){
			httpGet.setConfig(config);
		}
		if(httpPost != null){
			httpPost.setConfig(config);
		}
	}
	
	public static void main(String[] args) {
		downloadPage();
	}
	
	
}

