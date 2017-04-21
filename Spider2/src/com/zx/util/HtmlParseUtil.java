package com.zx.util;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zx.dto.AVUrl;

/**
 * html网页解析工具类,使用jsoup 想了想，就写一个不能复用的，只能爬取那个黄网的吧 是从有码女优列表开始爬取
 */
public class HtmlParseUtil {
	/**
	 * 从影片主页提取出想要的信息存入url实体类
	 * 所有影片的主页都是    https://www.javbus.co/xxx-yyy   xxx-yy是番号
	 */
	public static AVUrl getAVUrl(String html){
		Document doc = Jsoup.parse(html);
		
		
		
		//影片名字  它都写在<h3>里，且整页只有一个h3
		String avName = doc.select("h3").first().text();
		System.out.println("影片名字：" + avName);
		//封面图片
		String mainImageUrl = doc.select("a[class=\"bigImage\"]").first().attr("href");
		
		//下面获取到的这个div存储了很多影片信息
		Element infoDiv = doc.select("div[class=\"col-md-3 info\"]").first();
		Elements infos = infoDiv.select("p");//这个infoDiv下的所有p
		//番号 获取p集合中的第一个p中的第二个span中的值，也就是番号了
		String number = infos.get(0).select("span").get(1).text();
		//女优名字  这个div中的 所有class为star-name的就是女优名字了
		String bigSisterName = "";
		Elements bigSisterNames = infoDiv.select("div[class=\"star-name\"]");
		for (Element element : bigSisterNames) {
			bigSisterName += element.text() + ";";//把所有演员名字拼一起，用;割开
		}
		
		//磁力链接
		Set<String> resourceLinks = new HashSet<String>();
		Elements trs = doc.select("div[class=\"movie\"]").select("tr");//存着链接链表的table  中的所有tr
		for (Element tr : trs) {
			Element td = tr.select("td").first();//每个tr中的第一个td(保存着磁力链接和是否高清)
			Elements as = td.select("a");//只可能有 1或2个a.当2个a时，是高清的链接
			if(as.size() == 2){
				//当有2个a时。第一个a的href就是磁力链接   也就是说，只保存高清资源
				resourceLinks.add(as.first().attr("href"));
			}
		}
		
		//图片资源
		Set<String> imageUrls = new HashSet<String>();
//		Elements images = doc.select("div[id=\"sample-waterfall\"]").first().select("a");//这个div中的每个a标签就是一张图片
		Elements images = doc.select(".sample-box");//所有这个class的就是图片,是<a>
		for (Element image : images) {
			imageUrls.add(image.attr("href"));
		}
		
		
		//封装
		AVUrl avUrl = new AVUrl(avName, bigSisterName, number, resourceLinks, mainImageUrl, imageUrls);
		
		return avUrl;
	}

	/**
	 * 提取出女优列表中每个女优的主页链接 https://www.javbus.co/actresses/1（分页链接）
	 * https://www.javbus.co/actresses/2
	 */
	public static Set<String> getAVerMainPages(String html) {
		// 从字符串中获取html文件
		Document doc = Jsoup.parse(html);
		// 获取到有 所有女优列表 的div 用jquery的选择器方式 获取到的应该就只有一个
		Element mainDiv = null;
		/**
		 * 这里其实考虑的很不周全，一旦这里发生NullPointException。底下都会错
		 */
		try {
			mainDiv = doc.select("div[id=\"waterfall\"]").first();
		} catch (Exception e) {
			System.err.println("getAVerMianPage the mainDivs is null !");
			e.printStackTrace();
		}

		// 获取这个div中所有的<a>标签
		Elements as = mainDiv.select("a");
		// 取出a标签集合中的所有 href 这个集合中有该页所有女优的主页url
		Set<String> links = getLink(as, "href");

		/**
		 * 本来还应该取出下一页的链接，但它是按照页码的直接翻页，那么只要一直循环， 直到某个页码发生404错误即可。
		 */
		return links;
	}

	/**
	 * 我日。我智障。写完之后才发现这个方法和上面那个方法一模一样。真的一模一样！！！！ 也就是说。从女优列表获取所有女优主页的方法 和
	 * 从女优主页获取所有影片的方法是一样的
	 * 我又想了想。其实应该不一样。从女优主页获取影片url需要判断这个影片是不是高清，如果不是高清。不获取
	 */
	public static Set<String> getgetAVPages(String html) {
		// 从字符串中获取html文件
		Document doc = Jsoup.parse(html);
		// 获取到有 所有女优列表 的div 用jquery的选择器方式 获取到的应该就只有一个
		Element mainDiv = null;
		/**
		 * 这里其实考虑的很不周全，一旦这里发生NullPointException。底下都会错
		 */
		try {
			mainDiv = doc.select("div[id=\"waterfall\"]").first();
		} catch (Exception e) {
			System.err.println("getAVerMianPage the mainDivs is null !");
			e.printStackTrace();
		}

		// 获取这个div中所有的<a>标签
		Elements as = mainDiv.select("a");
		
		/**
		 * 这里要判断影片是不是高清的    只有这个a标签中含有class="btn btn-xs btn-primary"的<button>时，才是高清的
		 * 
		 * 这里之前有过几次下标越界的异常。我一直没找到愿意。现在仔细看了看。才发现。
		 * 原来是因为，如果有影片不是高清的，被我在中间删了。size就减少了。下一个索引也不对
		 * 正好我昨天无意间看到过一个解决这个的方法，就是倒序删除，容我试试。，。
		 */
		int asSize = as.size();
		for(int i =asSize-1; i >=0;i--){
			Elements temp = as.get(i).select("button[class=\"btn btn-xs btn-primary\"]");
			/**
			 * 看了下源码。这个elements不可能是空的。所以之前判断他是否为null是错误的
			 * 因为源码中的方法 reutrn 的时候。， 是 reutrn new Elements(element)；这样子的。
			 */
			if(temp.size() == 0){
				//为空表示不是高清，需要删除
				as.remove(i);
			}
		}
		
		// 取出a标签集合中的所有 href 这个集合中有该页所有女优的主页url
		Set<String> links = getLink(as, "href");

		/**
		 * 本来还应该取出下一页的链接，但它是按照页码的直接翻页，那么只要一直循环， 直到某个页码发生404错误即可。
		 */
		return links;
	}
	
	
	
	
	
	
	// /**
	// * 从女优主页提取出女优的所有影片主页（主页链接：https://www.javbus.co/star/xxx）
	// * 所有影片的主页都是 https://www.javbus.co/xxx-yyy xxx-yy是番号
	// */
	// public static Set<String> getAVPage(String html){
	// //从字符串中获取html文档
	// Document doc = Jsoup.parse(html);
	// //获取到影片列表的那个div 用jquery的选择器方式 获取到的应该就只有一个
	// Elements mainDivs = doc.select("div[id=\"waterfall\"]");
	// Element mainDiv = null;
	// //从元素集合中取出这唯一一个element
	// if(mainDivs != null && mainDivs.size() > 0){
	// mainDiv = mainDivs.first();
	// }
	// //取出所有的<a>
	// Elements as = mainDiv.select("a");
	// //获取a标签集合中所有的herf
	// Set<String> links = getLink(as, "href");
	//
	// return links;
	// }

	/**
	 * 从Elements中提取某个属性的值 因为考虑到一般就只是链接，所以用set防止重复
	 */
	private static Set<String> getLink(Elements elements, String attrName) {
		Set<String> attrValues = new HashSet<String>();
		// 遍历元素s,提取出所有属性值,并添加到set集合中
		for (Element element : elements) {
			String attrValue = element.attr(attrName);
			if (attrValue != null) {// 不为空，才加入集合
				attrValues.add(attrValue);
			}
		}
		return attrValues;
	}
}
