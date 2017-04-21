package com.zx.util;

import java.security.MessageDigest;

/**
 *将byte[]用MD5压缩
 */
public class MD5Util {
	/**
	 * 获取MD5字符串
	 */
	public static String getMD5(byte[] byteUrl){
		String s = null;
		//用来把字节转换成16进制表示的字符
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
		try {
			//使用java自带的 MessageDigest（消息摘要）类来实现MD5算法
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			//使用字节数组更新摘要
			messageDigest.update(byteUrl);
			//MD5的计算结果128位的长整数，用字节表示就是16个字节
			byte[] temp = messageDigest.digest(byteUrl);
			//每个字节用16进制表示的话，需要两个字符，（2^4 = 16   一个字节 8）所以需要16个字符
			char[] str = new char[32];
			//遍历MD5结果的16个字节，转换成16进制字符
			//这里使用k的原因是，要在一次循环中k++两次
			for(int i=0,k=0; i < 16 ;i++){
				byte b = temp[i];
				/**
				 * 取字节中高四位的数字转换
				 * >>>为逻辑右移，将符号位一起右移
				 */
				str[k++] = hexDigits[b >>> 4 & 0xf];
				//取字节中低四位数字转换
				str[k++] = hexDigits[b & 0xf];
			}
			//将转换后的char[]类型结果转换成字符串
			s = new String(str);
		} catch (Exception e) {
			System.err.println("getMD5 error");
			e.printStackTrace();
		}
		return s;
	}
}
