package com.zx.b;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *文件下载类 
 */
public class DownloadFile {
	/**
	 * 根据URL和网页类型生成需要保存的网页的文件名，去除URL中的非文件名字符
	 */
	public String getFileNameByUrl(String url,String contentType){
		//移除 http://
		url = url.substring(7);
		//如果是 text/html 类型
		if(contentType.indexOf("html") != -1){
			//把这些字符 都替换成 _
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
			return url;
		}else{
			//如果是application/pdf类型
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "." +
					contentType.substring(contentType.lastIndexOf('/')+1);
		}
	}
	
	/**
	 * 保存网页字节数组到本地文件，filePath 为要保存的文件的相对地址
	 * httpClient 返回回来的成了输入流。
	 */
	public void saveToLocal(InputStream in,String filePath){
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			//我自己多加了一个buffered的装饰
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			
//			//遍历 输出 字节数组
//			for(int i=0; i < data.length;i++){
//				dos.write(data[i]);
//			}
			/**
			 * 不明白为什么要一个一个字节输出
			 * ..这里这样写是用了缓冲区吗  不清楚
			 * dos.write(data);
			 * 这样写好像用不到缓冲区，直接执行的是FileOutputStream的write()方法
			 * 算了，还是用上面那中方式把
			 * dos.write(data);
			 */
			
			/**
			 * 上面的全都改了。现在传入的是输入流
			 */
			//把输入流包装成缓冲输入流
			bis = new BufferedInputStream(in);
			
			byte[] buffer = new byte[256];
			int bufferLen = buffer.length;
			int len = 0;
			while((len = bis.read(buffer, 0, bufferLen)) != -1){
				bos.write(buffer, 0, len);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bis.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 下载网页
	 * return  filePath
	 */
	public String downloadFile(String url){
		//文件保存路径
		String filePath = null;
		//创建HttpClient对象并设置参数, 设置请求重试处理类
		HttpClient httpClient = HttpClientBuilder.create().setRetryHandler(new DefaultHttpRequestRetryHandler()).build();
		//创建HttpGet
		HttpGet httpGet = new HttpGet(url);
		//设置HTTP  请求超时和连接超时时间都为5s  这是4.3版本后的设置超时时间方法
		RequestConfig config = RequestConfig.custom()
					.setSocketTimeout(5000).setConnectionRequestTimeout(5000).build();
		httpGet.setConfig(config);
		
		try {
			//执行请求
			HttpResponse response = httpClient.execute(httpGet);
			//获取请求状态码
			int statusCode = response.getStatusLine().getStatusCode();
			//判断 如果请求不成功
			if(statusCode != HttpStatus.SC_OK){
				System.out.println("请求失败:" + response.getStatusLine());
				return null;//返回的是filePath
			}
			
			
			//处理Http响应内容
			InputStream in = response.getEntity().getContent();
			/**
			 * 这个HttpClient获取返回头参数的最新版本的方法又找不到，
			 * 自己看了下，源码，getHeaders()这个方法是把所有name为传入的那个参数的值返回回来
			 */
			Header[] headers  = response.getHeaders("Content-Type");
			/**
			 * 传回来的是一个Header类型的数组，为了保险期起见，我再确认下它的name为Content-Type
			 */
			for (Header header : headers) {
				if(header.getName().equals("Content-Type")){
					filePath = "F:/crawler/" + getFileNameByUrl(url, header.getValue());
				}
			}
			
			//如果FilePath为空，表示没有获取到Header中的Content-Type,我准备给它设个默认的名字
			if(filePath == null)
				filePath = "F:/crawler/" + System.currentTimeMillis();
			
			//保存到本地
			saveToLocal(in, filePath);
			
			
			
		} catch(SocketTimeoutException e){
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return filePath;
	}
	
	
}
