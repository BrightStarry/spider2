package com.zx.b;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * HTML解析工具类
 * 用来把每个网页中的URL提取出来 ，以便放入队列
 * 也可以提取其他内容
 */
public class HtmlParseTool {
	/**
	 * 获取一个网站的所有链接 LinkFilter用来过滤链接(目前还没有)
	 * 
	 * 书上原本用的是HTML-PARSER这个项目进行解析，但这个项目从2006年开始就没有更新了。
	 * 所以网上推荐使用Jsoup来解析。
	 * 所以底下这些解析都只靠自己摸索了
	 */
	public static Set<String> extracLinks(String url/*,LinkFilter filter*/){
		//存储 提取出的 链接数组
		Set<String> links = new HashSet<String>();
		
		/**
		 * 使用Jsoup解析html
		 */
		Document document = null;
		try {
			//根据Url建立连接获取HTML文件
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			/**
			 * 如果上面的获取失败，使用下面的方法再次实验
			 */
			System.err.println("Jsoup的第一种获取html的方法失败，正在尝试使用第二种");
			try {
				document = Jsoup.connect(url)
						.data("query","java").
						userAgent("Chrome")//浏览器名字？我不知道用这个可以不
						.cookie("auth", "token")
						.post();
			} catch (IOException e1) {
				//再次失败。。。无解
				e1.printStackTrace();
			}
		}
		try {
			/**
			 * 从frame标签中提取出src的url...顺路在保存到链接集合中去好了
			 * 看了下。获取到的是ELEMENTS，这个类继承自ArrayList<Element>类。。
			 * （反编译器真的是好用，不用费劲心思找源码了）
			 * 据说可以用jquery的语法获取元素节点
			 */
//		//找到所有frame元素
//		Elements frame = document.select("frame");
//		//遍历frame元素的list集合
//		for (Element element : frame) {
//			String newUrl = element.attr("src");//从frame的src属性中取出URL
//			//如果不为空，就加入链接集合
//			if(newUrl != null && newUrl.trim().length() >0)
//				links.add(newUrl);
//		}
			extracAttrValueByDoc(links, document, "frame","src",url);
			/**
			 * 获取所有a标签中的src的URL
			 */
			extracAttrValueByDoc(links, document, "a", "href",url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return links;
	}
	
	/**
	 * 基于Jsoup
	 * 从一个doc中提取若干个elements中的attr属性 的值
	 * 主要是我觉得frame标签要写一个提取，a又要写
	 */
	private static void extracAttrValueByDoc(Set<String> links,Document document,
			String elementName,String attr,String url){
			Elements elements = document.select(elementName);
			if(elements == null){
				return;
			}
			for (Element element : elements) {
				String newUrl = element.attr(attr);
				
				if(newUrl != null && newUrl.trim().length()>0){
					//如果链接不为空，但是没有指定的网站的域名。就给它加上域名。让他成为绝对路径
					//我的想法是截取到第一个字到最后一个/的位置
					//但如果这个网站是有其他的域名的。还是不允许 如果查不到// 应该就是没有http开头的了
					/**
					 * 现在的问题就是  那种网站的如果需要翻页。但是每个页码都是相对路径，所以需要在前面
					 * 加上截取到最后一个/的当前url。（这样需要把当前的URL也传过来）
					 * 而且，不允许访问到其他网站
					 */
					if(newUrl.indexOf("www") == -1 && newUrl.indexOf("http") == -1){
						System.out.println("----------有一个相对路径 :"+ newUrl);
						if(newUrl.charAt(0) == '/'){
							newUrl = "http://www.mmjpg.com" + newUrl;
						}else{
//							int temp = url.lastIndexOf('/');
//							if(temp != 6){
//								System.out.println("前缀URL:"+url);
//								newUrl = url.substring(0, temp) + newUrl;
//							}else{
//								newUrl = url + newUrl;
//							}
							return;
						}
						
						
						System.out.println(newUrl);
					}else if(newUrl.indexOf("mmjpg") == -1){
						return;
					}
					
					
					
					links.add(newUrl);
				}
			}
	}
	
}
