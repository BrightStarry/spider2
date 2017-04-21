package com.zx.dto;

import java.util.Set;

/**
 *因为这次要爬取的是   黄色网站，所以需要一个继承spiderUrl的特定格式的实体类 
 */
public class AVUrl extends SpiderUrl{
	private static final long serialVersionUID = 1L;
	
	private String avName;//影片名字
	private String bigSisterName;//大姐姐名字233
	private String number;//番号
	private boolean isDistinct;//是否高清
	private boolean isHorse;//有没有码 233
	private Set<String> resourceLinks;//磁力链接集合 magnet
	private String mainImageUrl;//封面图url
	private Set<String> imageUrls;//浏览图集合
	
	
	
	public AVUrl(String avName, String bigSisterName, String number, Set<String> resourceLinks, String mainImageUrl,
			Set<String> imageUrls) {
		this.avName = avName;
		this.bigSisterName = bigSisterName;
		this.number = number;
		this.resourceLinks = resourceLinks;
		this.mainImageUrl = mainImageUrl;
		this.imageUrls = imageUrls;
	}
	public String getAvName() {
		return avName;
	}
	public void setAvName(String avName) {
		this.avName = avName;
	}
	public String getBigSisterName() {
		return bigSisterName;
	}
	public void setBigSisterName(String bigSisterName) {
		this.bigSisterName = bigSisterName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public boolean isDistinct() {
		return isDistinct;
	}
	public void setDistinct(boolean isDistinct) {
		this.isDistinct = isDistinct;
	}
	public boolean isHorse() {
		return isHorse;
	}
	public void setHorse(boolean isHorse) {
		this.isHorse = isHorse;
	}
	public Set<String> getResourceLinks() {
		return resourceLinks;
	}
	public void setResourceLinks(Set<String> resourceLinks) {
		this.resourceLinks = resourceLinks;
	}
	public String getMainImageUrl() {
		return mainImageUrl;
	}
	public void setMainImageUrl(String mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}
	public Set<String> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(Set<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	
	
}
