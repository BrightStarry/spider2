package com.zx.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *httpclient 请求工具类
 */
public class HttpClientUtil {
	
	/**
	 * 向指定url页面发起请求。返回response
	 */
	public static CloseableHttpResponse getHttpResponse(String url){
		CloseableHttpResponse httpResponse = null;
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
			HttpGet httpGet = new HttpGet(url);
			httpResponse = httpClient.execute(httpGet);
		} catch (Exception e) {
			System.err.println("httpClient request error!");
			e.printStackTrace();
		}
		return httpResponse;
	}
}
