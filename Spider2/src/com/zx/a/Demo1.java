package com.zx.a;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**简单发送请求获取返回的HTTP状态码例子*/
public class Demo1 {
	
	public static void main(String[] args) throws Exception {
		/**
		 * httpGet请求
		 */
		//可关闭的httpClient
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		//创建一个HttpGet
		HttpGet httpGet = new HttpGet("http://www.baidu.com");
		//获取响应对象
		HttpResponse response = httpClient.execute(httpGet);
		//输出HTTP状态码
		System.out.println(response.getStatusLine().getStatusCode());
		//释放连接
		httpGet.releaseConnection();
		
		/**
		 * httpPost请求
		 */
		//创建一个httpPost
		HttpPost httpPost = new HttpPost("http://www.zdfans.com");
		//使用数组来添加post的参数   -- 使用 键值对  作为泛型
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//设置参数  注意：实例化的是 标准键值对 对象
		nameValuePairs.add(new BasicNameValuePair("a","a"));
		//执行httpPost请求
		HttpResponse response2 = httpClient.execute(httpPost);
		//输出状态码
		System.out.println(response2.getStatusLine().getStatusCode());
		//释放连接
		httpPost.releaseConnection();
		httpClient.close();
		
	}
}
