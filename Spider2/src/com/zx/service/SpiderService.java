package com.zx.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zx.dao.RedisUtil;
import com.zx.dto.AVUrl;
import com.zx.util.Constant;
import com.zx.util.HtmlParseUtil;
import com.zx.util.WebClientUtil;

/**
 *爬虫服务 类 
 */
public class SpiderService {
	/**
	 * 主进程
	 */
	public static void main(String[] args) {
		RedisUtil.initToDo(new String[]{"https://www.javbus.co/star/p84"});
//		loop1();
		loop2();
		loop3();
	}
	
	/**
	 * 首先要把女优列表页爬完
	 * 这个因为是自己定义的todo，就是 url+page，所以不需要保存到已爬取页面
	 */
	public static void loop1(){
		String url = "https://www.javbus.co/actresses/";
		
		//如果  这个分页链接 不为 404 一直爬取  爬个20页就好了
		int httpStatus = 0;
		int page = 1;
		do{	
			System.out.print("开始抓取第" + page + "页");
			CloseableHttpResponse response = null;
			try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
				HttpGet httpGet = new HttpGet(url + page);
				response = httpClient.execute(httpGet);
				
				//如果没成功，跳出本次循环
				httpStatus = response.getStatusLine().getStatusCode();
				System.out.println("--------获取的状态码：" + httpStatus);
				if(httpStatus != 200){
					continue;
				}
				
				//获取html
				InputStream in = response.getEntity().getContent();
				//in转string
				String html = IOUtils.toString(in,"UTF-8");
				
				//获取女优主页面URL集合
				Set<String> aVerMainPages = HtmlParseUtil.getAVerMainPages(html);
				//入队
				for (String string : aVerMainPages) {
					System.out.println("入队 的URL：" + string);
					//这个1000-page是权重，页数越靠前，权重越高
					RedisUtil.enToDo(Constant.TODO,1000-page, string);
				}
				
				page++;
			} catch (Exception e) {
				System.err.println("第"+ page + "页获取 错误！");
				e.printStackTrace();
			}finally{
				try {
					response.close();
				} catch (IOException e) {
					System.out.println("close resposne error");
					e.printStackTrace();
				}
			}
		}while(httpStatus != 404 && page <2);
		System.out.println("步骤1（所有女优主页） success！");
	}
	
	/**
	 * 从女优列表中爬取所有影片url
	 */
	public static void loop2(){
		while(!RedisUtil.isEmptyToDo(Constant.TODO)){
		//从todo中获取url 这个获取到的链接使这样的 https://www.javbus.co/star/b6b
		String url = RedisUtil.deToDo(Constant.TODO);
		
		int page = 1;
		int statusCode = 0;
			//当爬取完所有女优主页 退出循环
			while(statusCode != 404){
				CloseableHttpResponse response = null;
				
				try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
					
					System.out.println("正在爬取的链接："+ url + "/" + page);
					//从todo中获取url   请求这个url 获取响应
					/**
					 * 这里之前的问题好像在于。如果是第一页。不应该加上 /1。不然好像会重定向。就获取不到html了。
					 * 不过使用htmlunit应该可以
					 */
					HttpPost httpGet = new HttpPost(page == 1 ? url : url + "/" + page);
					response = httpClient.execute(httpGet);
					
					//如果没有获取成功 结束该次循环
					statusCode = response.getStatusLine().getStatusCode();
					if(statusCode != 200){
						continue;
					}
					
					//获取html页面
					String html = IOUtils.toString(response.getEntity().getContent(),"UTF-8");
					//获取该女优主页中的所有影片URL
					Set<String> pages = HtmlParseUtil.getgetAVPages(html);
					//所有待爬取url入队
					for (String string : pages) {
						System.out.println("已入队的url:"  + string);
						RedisUtil.enToDo(Constant.TODO_MAIN, 1000-page, string);
					}
					/**
					 * 已爬取URL入队
					 * 因为上一步的关系，这步基本也没有重复的可能性，
					 * 所以也不需要入队
					 */
					page++;
				} catch (Exception e) {
					System.err.println("步骤2：失败 ,此时，页数为:" + page);
					e.printStackTrace();
				}finally{
					try {
						if (response !=null) {
							response.close();
						}
					} catch (IOException e) {
						System.out.println("close resposne error");
						e.printStackTrace();
					}
					
				}
				//放在这里减少，是为了在错误时输出权重为几的时候出错了
				
			}
		}
		System.out.println("步骤2（所有影片主页） success!");
	}
	
	/**
	 * 步骤3.需要重新写。采用htmlunit的请求方法获取html页面
	 * 因为使用httpClient无法获取ajax加载后的页面
	 */
	public static void loop3(){
		//当todo_main 为空，爬完了，就停止
		while(!RedisUtil.isEmptyToDo(Constant.TODO_MAIN)){
			//使用try-with-resources  自动关闭资源
			try(WebClient webClient = WebClientUtil.getWebClient();) {
				//url出队
				String url = RedisUtil.deToDo(Constant.TODO_MAIN);
				System.out.println("正在解析：" + url);
				
				//获取page  这个把Page向下转型成HtmlPage。我不知道为什么要这么做，但网上别人都这么写
				HtmlPage page = (HtmlPage)webClient.getPage(url);
				//设置webClient 的js相关属性
				WebClientUtil.setJS(webClient);
				/**
				 * 嘿嘿。看源码找到了http状态码的属性
				 */
				int statusCode = page.getWebResponse().getStatusCode();
				if(statusCode != 200){
					continue;
				}
				//获取html
				String html = page.asXml();
				
				//解析html
				AVUrl avUrl = HtmlParseUtil.getAVUrl(html);
				
				//保存
				RedisUtil.saveAVUrl(avUrl);
			} catch (Exception e) {
				System.err.println("步骤3.爬取失败");
				e.printStackTrace();
			}
		}
		System.out.println("success");
	}
	
	
	/**
	 * 步骤3 ： 暂且只将每个影片信息存到本地
	 */
//	public static void loop3(){
//		//当todo_main 为空。爬完了。就停止
//		while(!RedisUtil.isEmptyToDo(Constant.TODO_MAIN)){
//			CloseableHttpResponse response = null;
//			
//			try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
//				//获取todomain中的url
//				String url = RedisUtil.deToDo(Constant.TODO_MAIN);
//				System.out.println("正在解析：" + url);
//				//获取响应
//				HttpGet httpGet = new HttpGet(url);
//				response = httpClient.execute(httpGet);
//				/**
//				 * 这次url需要入已爬取队列
//				 * 转换成MD5形式入队
//				 * 好了。已经不需要了
//				 */
//				//RedisUtil.enVisited(MD5Util.getMD5(url.getBytes()));
//				
//				//如果不成功，结束该次循环
//				if(response.getStatusLine().getStatusCode() != 200){
//					continue;
//				}
//				//获取html
//				String html = IOUtils.toString(response.getEntity().getContent(),"UTF-8");
//				//解析html获取实体类
//				AVUrl avUrl = HtmlParseUtil.getAVUrl(html);
//				
//				/**
//				 * 于是我悲伤地发现。这个也不需要入已爬取队列。因为没有重复的可能性
//				 */
//				RedisUtil.saveAvUrl(avUrl);
//				
//			} catch (Exception e) {
//				System.err.println("步骤3 error");
//				e.printStackTrace();
//			}finally{
//				try {
//					if(response != null)
//					response.close();
//				} catch (IOException e) {
//					System.err.println("response close error!");
//					e.printStackTrace();
//				}
//			}
//		}
//		System.out.println("一将功成万骨枯！成功了2333333333333");
//	}
}
