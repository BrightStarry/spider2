package com.zx.dao;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

import com.zx.dto.AVUrl;
import com.zx.util.Constant;
import com.zx.util.FilePathUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis数据库 工具类
 */
public class RedisUtil {
	// 连接池
	private static JedisPool pool = null;

	/**
	 * 构建连接池
	 */
	private static JedisPool getPool() {
		if (pool == null) {
			// redis连接池配置类
			JedisPoolConfig config = new JedisPoolConfig();
			// 应该是最大连接数，百度上是maxActive，但没有这个方法
			// -1表示不限制
			config.setMaxTotal(-1);
			// 一个pool最多有多少个状态为idle(空闲的)的实例
			config.setMaxIdle(5);
			// 当borrow(引入)jedis连接时，超时时间，如果超时，抛出JedisConnectionException
			config.setMaxWaitMillis(1000 * 100);// 10s?
			// 在borrow一个redis实例时，是否提前验证，如果为true，保证获取到的redis实例都是可用的
			config.setTestOnBorrow(true);
			pool = new JedisPool(config, "127.0.0.1", 6379);
		}
		return pool;
	}

	/**
	 * 关闭连接   
	 * 考虑到使用java7的try-with-resources必须要用close()方法。改名
	 */
	public static void close(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception e) {
				System.err.println("closeing jeids Connection error!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取连接
	 */
	private static Jedis getJedis() {
		Jedis jedis = null;
		try {
			jedis = getPool().getResource();
		} catch (Exception e) {
			System.err.println("get redis connection error!");
			e.printStackTrace();
		}
		return jedis;
	}

	/**
	 * 删除指定key的所有数据
	 * 因为要爬取分三个步骤，所以需要在爬完一个todo队列后，先删除。再爬取
	 */
	public void deleteKey(String key){
		try(Jedis jedis = getJedis();) {
			jedis.del(key);
		} catch (Exception e) {
			System.err.println("deleteKey  error!");
			e.printStackTrace();
		}
	}

	/**
	 * 待爬取URL  入队
	 */
	public static void enToDo(String queueName,double score,String url){
		try(Jedis jedis = getJedis();) {
			//添加数据  键  权重（分数） 值  sorted Set
			jedis.zadd(queueName, score, url);
		} catch (Exception e) {
			System.err.println("enToDo error!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 待爬取URL 出队
	 */
	public static String deToDo(String queueName){
		/**
		 * 因为取出的是一个set集合，所以需要先使用迭代器迭代，
		 * 因为每次取出的set的size就是1，所以直接next就好了
		 * 如果出队错误，返回null
		 */
		Jedis jedis = getJedis();
		String url = null;
		try {
			//按照权重排序后读取范围内元素
			Set<String> urls = jedis.zrange(queueName, 0, 0);
			//迭代取出url
			url = urls.iterator().next();
			//删除该已出队元素
			jedis.zrem(queueName,url);
			return url;
		} catch (Exception e) {
			System.err.println("deToDo error!");
			e.printStackTrace();
		}finally{
			jedis.close();
		}
		return null;
	}
	
	/**
	 * 待爬取URL 数量
	 * 这里返回long的原因是zcard方法返回的就是long
	 */
	public static long getToDoLength(String queueName){
		long length = 0;
		try(Jedis jedis = getJedis();){
			length = jedis.zcard(queueName);
		}catch(Exception e){
			System.err.println("getToDoLength error!");
			e.printStackTrace();
		}
		return length;
	}
	
	/**
	 * 判断待爬取URL队列是否为空
	 */
	public static boolean isEmptyToDo(String queueName){
		return getToDoLength(queueName) == 0 ? true : false;
	}
	
	
	/**
	 * 已爬取URL 入队
	 * 直接把MD5转换过了的url存进去，比对的时候只要试着取出对应MD5URl的值，有就重复
	 */
	public static void enVisited(String url){
		try (Jedis jedis = getJedis()){
			jedis.set(url,null);
		} catch (Exception e) {
			System.err.println("en Visited error!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 已爬取URL 判断是否存在
	 */
	public static boolean isEmptyVisited(String url){
		boolean flag = true;
		try (Jedis jedis = getJedis()){
			flag = jedis.exists(url);
		} catch (Exception e) {
			System.err.println("isEmptyVisited  error!");
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	
	/**
	 * 保存爬取出来的结果实体类 
	 * 直接把spiderURl实体类 用hash的形式存入redis
	 * 我想了想。这里先直接存到本地试试吧
	 */
//	public static void saveAvUrl(AVUrl avUrl){
//		//首先需要获取存储路径 并返回File
//		File file = FilePathUtil.getPath(avUrl);
//		
//		try(BufferedWriter bw = new BufferedWriter(
//				new OutputStreamWriter(
//						new FileOutputStream(file),"UTF-8"))){
//			System.out.println(file.getName());
//			//封面图url
//			String mainImageUrl = avUrl.getMainImageUrl();
//			bw.write("封面图片:  " + mainImageUrl);
//			bw.newLine();
//			bw.newLine();
//			//磁力链接
//			Set<String> resourceLinks = avUrl.getResourceLinks();
//			for (String string : resourceLinks) {
//				bw.write("磁力链接 : " + string);
//				bw.newLine();
//			}
//			bw.newLine();
//			bw.newLine();
//			//其它图片链接
//			Set<String> imageUrls = avUrl.getImageUrls();
//			for (String string : imageUrls) {
//				bw.write("其他图片: " + string);
//				bw.newLine();
//			}
//		} catch (Exception e) {
//			System.err.println("saveAvUrl error!");
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * 换种方式，直接保存成html文件保存到本地
	 */
	public static void saveAVUrl(AVUrl avUrl){
		//获取保存路径
		File file = FilePathUtil.getPath(avUrl);
		
		try(BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(file),"UTF-8"));){
			//创建html文档的str
			StringBuilder html = new StringBuilder("<!doctype html><html><head><meta charset=\"UTF-8\">");
			html.append("<title>" + avUrl.getAvName() + "</title>");//用片名做标题
			html.append("</head><body>");
			
			html.append("<h3>" + avUrl.getAvName() + "</h3>");//片名
			html.append("<img src=\"" + avUrl.getMainImageUrl() + "\"><hr>" );//封面图
			
			Set<String> links = avUrl.getResourceLinks();//磁力链接
			for (String string : links) {
				html.append("<p>" + string + "</p>");
			}
			html.append("<hr>");
			
			Set<String> imageUrls = avUrl.getImageUrls();//图片
			for (String string : imageUrls) {
				html.append("<img src=\"" + string + "\">" );
			}
			html.append("<hr>");
			
			html.append("<p>" + avUrl.getBigSisterName() + "</p>");//出演女优名
			
			html.append("</body></html>");
			
			bw.write(html.toString());
		}catch (Exception e) {
			System.err.println("saveavurl error！");
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化爬取队列
	 * ..这个方法也没有存在的必要了TT
	 */
	public static void initToDo(String...seedUrl){
		try {
			for (String url : seedUrl) {
				enToDo(Constant.TODO, 1000, url);
			}
		} catch (Exception e) {
			System.err.println("initToDo error!");
			e.printStackTrace();
		}
		
	}

}
