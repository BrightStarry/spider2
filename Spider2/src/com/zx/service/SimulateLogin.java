package com.zx.service;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 *试着能不能登录学校的毕业实践网站 byzj.zjjy.net 
 */
public class SimulateLogin {
	
	public static void main(String[] args) throws Exception {
		//模拟谷歌浏览器
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setTimeout(10000);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		
		
		HtmlPage page = (HtmlPage)webClient.getPage("http://bysj.zjjy.net");
		//单击事件
		page.getElementsByName("Cs_Admin_Flag").get(1).click();
		//填写账号密码
		HtmlInput username = (HtmlInput) page.getElementById("Cs_Admin_Name");
		HtmlInput password = (HtmlInput) page.getElementById("Cs_Admin_Pass");
		
		username.setAttribute("value", "50140127");
		password.setValueAttribute("165616");//一样的写法
		
		//点击登录
		HtmlPage registeredPage  = (HtmlPage)page.getElementById("Body_Right_Button").click();//登录后页面
		webClient.waitForBackgroundJavaScript(3000);
		
		HtmlPage mainPage = (HtmlPage)registeredPage.getElementsByTagName("a").get(1).click();//进入主页面
		webClient.waitForBackgroundJavaScript(3000);
		
		HtmlPage noteMainPage = (HtmlPage)mainPage.getElementsByTagName("a").get(14).click();//周记主页面
		webClient.waitForBackgroundJavaScript(3000);//这几句很重要。特别是这里。周记是要js加载出来的
		
		HtmlPage notePage = noteMainPage.getElementsByTagName("a").get(0).click();//写周记的页面
		webClient.waitForBackgroundJavaScript(1000);
		
		
		String str = "遇到bug并解决了，详细把bug表现描述出来，并把解决经过写出来，做成笔记，就算以后"
				+ "不翻看，这样至少会加深你对类似bug的印象，下回就会知道类似的问题如何解决；程序执行"
				+ "缓慢，首先应该检查数据结构是否合理，然后检查遍历这个数据结构的遍历语句是否写复杂了"
				+ "，能不能把遍历降低；遇到bug可以与周围的同事或朋友进行探讨，别人的思路可能会给你帮助"
				+ "，也可能别人曾经遇到过类似的问题。第一条最重要，善于记录问题和解决问题方案。个人的一"
				+ "些bug解决经验以及测试方面的分析：输入的内容，是否有最短或最长数据限制；可能会产生多"
				+ "个数据的，尽量试试非常多的数据进行测试；写遍历的时候，特别是多重遍历，考虑是否会产生"
				+ "无限的遍历计算（非常大的计算量）；做数据库删除的时候，考虑数据删除条件是否完全正确，修"
				+ "改和查询同样如是；存入数据的时候，验证数据格式，以及考虑换行或特殊字符会造成前"
				+ "端json解析错误，此处不仅仅是指html层面的代码或JavaScript的代码造成注入漏洞；"
				+ "更改了程序之后，尽量考虑周到有哪些地方会受到影响，最好是在写注释的时候注明有哪些地方"
				+ "会产生调用数据操作的时候一定要验明权限，验证当前用户是否有权限做修改，包括当前数据是否属"
				+ "于当前用户等；对一个数据操作之前，不要凭着想法，觉得是对的，一定要用程序验明是否存在，并对验明"
				+ "的结果进行用户提示或者是报错处理；此处分为用户存数据到数据库之前验证，另一个就是取数据并进行操"
				+ "作的时候进行验证，特别是没有规定用户必填，但是在显示或者操作时候却需要用到的数据，一定要验明；"
				+ "浮点数的计算丢失精度的问题，这个做稍微复杂的项目会遇到，此处把参加计算的数据都进行浮点型格式化"
				+ "并把得到的结果按需求四舍五入或其他方式取值，这种做法一般不会造成精度丢失；两个数参与除法运算，"
				+ "必须检查除数是否会存在为0的情况，这种业务逻辑一般可能涉及到计算用户好评率，计算用户平均评星的情"
				+ "况，虽然这个除数不能为0是小学的知识点，但是开发过程中可能会漏掉监测，造成程序运算错误。";
				
				
		
		/**
		 * 需要注意的是textarea没有value属性，所以直接添加会有问题,需要把文本嵌套在标签中
		 */
		notePage.getElementById("Journal_Content").setTextContent(str);
		//设置一个计算字符的div中的value
		notePage.getElementById("Jour_Count").setAttribute("value", "600");
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage successPage = (HtmlPage)notePage.getElementsByName("button").get(0).click();
		
		webClient.waitForBackgroundJavaScript(3000);
		System.out.println(successPage.asXml());
		
		
		
		webClient.close();
	}
	
}
