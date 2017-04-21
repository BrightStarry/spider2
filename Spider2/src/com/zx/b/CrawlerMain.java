package com.zx.b;

import java.util.Set;

/**
 *爬虫主程序 
 */
public class CrawlerMain {
	//主方法入口
	public static void main(String[] args) {
		CrawlerMain crawler =new CrawlerMain();
		String[] seeds = new String[]{"http://www.mmjpg.com/mm/835"};
		crawler.crawling(seeds);
	}
	
	
	/**
	 * 使用种子初始化URL队列
	 */
	public void initCrawlerWithSeeds(String[] seeds){
		for(String seed : seeds){
			//将所有种子URL添加到未访问URL队列中
			LinkQueue.unVisitedUrlEnQueue(seed);
		}
	}
	
	/**
	 * 抓取过程...爬行
	 * 离胜利只有一步之遥了。，小伙子。。为了一万部黄片。。啊，不对。为了成为最强的架构师，
	 */
	public void crawling(String[] seeds){
		//初始化URL队列
		initCrawlerWithSeeds(seeds);
		
		System.out.println("初始化队列成功");
		
		//只有未访问URL队列不为空 且 爬取的网站数量小于 1000 才继续
		while(!LinkQueue.unVisitedUrlIsEmpty() && LinkQueue.getVisitedUrlNum() < 1000){
			//未访问队列 队头 出队列
			String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
			//我已经在提取链接的时候判断过不为空了，所以不需要这一步
//			if(visitUrl == null)//如果为空，跳过该次循环
//				continue;
			//创建文件下载类
			DownloadFile downloadFile = new DownloadFile();
			//下载网页
			try {
				downloadFile.downloadFile(visitUrl);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			System.out.println("下载网页成功");
			//URL入 已访问 队列
			LinkQueue.addVisitedUrl(visitUrl);
			//取出下载网页中所有的 链接
			Set<String> links = HtmlParseTool.extracLinks(visitUrl);
			//将提取出的链接 入队
			for (String str : links) {
				LinkQueue.unVisitedUrlEnQueue(str);
			}
		}
	}
}
