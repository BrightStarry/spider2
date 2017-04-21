package com.zx.util;

import java.io.File;

import com.zx.dto.AVUrl;

/**
 *按照影片信息，设置对应的文件存储路径 
 */
public class FilePathUtil {
	
	/**
	 *获取影片存储路径 
	 */
	public static File getPath(AVUrl avUrl){
		StringBuilder path = new StringBuilder("F:/sipder");
		
		//如果是多女优出演，统一保存到 合集
		String[] bigSisterName = avUrl.getBigSisterName().split(";");
		if(bigSisterName.length != 1){
			path.append("/合集");
		}else{
			//如果就一个，就用那个女优的名字作文件夹名字
			path.append("/" + bigSisterName[0]);
		}
		
		//然后文件名用番号 + 影片名(因为那个网站上的片名就是这样，所以直接用片名就可以了)
		path.append("/" + avUrl.getAvName() + ".html");
		
		/**
		 * 如果父级目录不存在，创建
		 */
		File file = null;
		try {
			file = new File(path.toString());
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
		} catch (Exception e) {
			System.err.println("创建文件目录失败!");
			e.printStackTrace();
		}
		
		return file;
	}
}
