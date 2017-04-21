package com.zx.dto;

import java.io.Serializable;

/**
 *url实体类
 */
public class SpiderUrl implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String url;//网址
	private double score;//权重
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
}
