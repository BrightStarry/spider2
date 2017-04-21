package com.zx.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class HtmlUnitTest {
	public static void main(String[] args) throws Exception {
		//模拟一个浏览器  谷歌浏览器
		WebClient webClient = new WebClient(BrowserVersion.CHROME);//设置浏览器的User-Agent
		webClient.getOptions().setThrowExceptionOnScriptError(false); //脚本运行错误时是否抛出异常
	    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);  //失败的状态码时是否抛出异常
	    webClient.getOptions().setJavaScriptEnabled(true);  //js是否加载
        webClient.getOptions().setActiveXNative(false);  //？这个好像是加载flash的控件
        webClient.getOptions().setCssEnabled(false);  //css是否加载
        
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持ajax
        webClient.getOptions().setTimeout(30000);//设置“浏览器”的请求超时时间
        webClient.getOptions().setRedirectEnabled(true);//是否启用重定向
        
        //打开网页
        HtmlPage page = (HtmlPage)webClient.getPage("https://www.javbus.co/CPDE-005");
        webClient.waitForBackgroundJavaScript(600*1000);  //设置JS后台等待执行时间，这个好像是要在获取页面之后设置才有效
        webClient.setJavaScriptTimeout(10000);//设置JS执行的超时时间 。一样。都是在获取页面之后设置的
        
        //在这个等待的循环中，别人好像都执行了一些解析页面元素的方法。。。而我是使用jsoup进行解析的，。所以只能够等待了
        /*for(int i=0;i <20; i++){
        	 synchronized (page) {
                 page.wait(500);
            }
        }*/
        
        String html = page.asXml();
        System.out.println(html);
        webClient.close();
	}
}
