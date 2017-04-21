package com.zx.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 *	使用htmlunit的工具类
 */
public class WebClientUtil {
	
	/**
	 * 获取webClient
	 */
	public static WebClient getWebClient(){
		//默认使用谷歌浏览其器
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		//
		webClient.getOptions().setActiveXNative(false);//不启用flash
		webClient.getOptions().setCssEnabled(false);//不启用css
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//不抛出状态码不为200的异常
		webClient.getOptions().setThrowExceptionOnScriptError(false);//不抛出脚本异常
		webClient.getOptions().setRedirectEnabled(true);//启用重定向
		webClient.getOptions().setTimeout(10000);//设置浏览器请求超时时间
		
		webClient.getOptions().setJavaScriptEnabled(true);//启用js
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());//支持ajxa
		
		return webClient;
	}
	
	/**
	 * 设置好加载ajax的相关属性
	 */
	public static void setJS(WebClient webClient){
		webClient.waitForBackgroundJavaScript(5000);//设置js后台执行等待时间
		webClient.setJavaScriptTimeout(5000);//js等待超时时间
	}
}
