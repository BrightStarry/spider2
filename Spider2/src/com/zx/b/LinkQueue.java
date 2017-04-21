package com.zx.b;

import java.util.HashSet;
import java.util.Set;

/**
 *队列 保存 已访问过的URL
 *考虑到不能重复、快速查找 使用 HashSet 
 */
public class LinkQueue {
	//已访问的URL 集合
	private static Set<String> visitedUrl = new HashSet<String>();
	//未访问的URL 集合 就是把刚才创建的Queue作为成员变量（组合）
	private static Queue unVisitedUrl = new Queue();
	
	/**
	 * 获取未访问的URL队列
	 */
	public static Queue getUnVisitedUrl(){
		return unVisitedUrl;
	}
	
	/**
	 * 添加到 已访问的URL队列
	 */
	public static void addVisitedUrl(String url){
		visitedUrl.add(url);
	}
	
	/**
	 * 删除 已访问过的URL
	 */
	public static void removeVisitedUrl(String url){
		visitedUrl.remove(url);
	}
	
	/**
	 * 未访问的Url 出队列
	 */
	public static Object unVisitedUrlDeQueue(){
		return unVisitedUrl.deQueue();
	}
	
	/**
	 * 未访问的Url 入队列  
	 * 确保每个URL只访问一次
	 */
	public static void unVisitedUrlEnQueue(String url){
		if(url != null 
				&& url.trim().length() > 0 
					&& !visitedUrl.contains(url) 
						&& !unVisitedUrl.Contains(url)){
			//如果 url 不为空 且 它在已访问的和未访问的URL队列中都不存在，入队
			unVisitedUrl.enQueue(url);
		}
		
	}
	
	/**
	 * 获取已访问的URL的数目
	 */
	public static int getVisitedUrlNum(){
		return visitedUrl.size();
	}
	
	/**
	 * 判断未访问的URL队列是否为空
	 * 不存在  True 存在  False
	 */
	public static boolean unVisitedUrlIsEmpty(){
		return unVisitedUrl.isEmpty();
	}
	
}
