package com.zx.b;

import java.util.LinkedList;

/**
 * 队列，保存 未访问的URL
 *使用linkedList 
 */
public class Queue {
	//未访问的URL队列
	private LinkedList<Object> queue = new LinkedList<Object>();
	
	/**
	 * 入队
	 */
	public void enQueue(Object object){
		queue.add(object);
	}
	/**
	 * 出队
	 * 出队的意思就是删除，因为每次都是取出第一个值去爬，爬完，把第一个值删了，也就是出队了
	 * 这个removeFirst方法应该是返回最后一个值，并且在队列中删除最后一个值
	 */
	public Object deQueue(){
		return queue.removeFirst();//返回的是删除了的那个值
	}
	
	/**
	 * 判断队列是否为空
	 * 不存在返回True ，存在为 false
	 */
	public boolean isEmpty(){
		return queue.isEmpty();
	}
	
	/**
	 * 判断队列是否包含x
	 */
	public boolean Contains(Object object){
		return queue.contains(object);
	}
}
