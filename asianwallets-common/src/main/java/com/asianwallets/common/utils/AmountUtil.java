package com.asianwallets.common.utils;

public class AmountUtil {
	/**
	 * 字符串长度不够时，补0
	 * @param str
	 * @param strLength
	 * @param position 左侧/右侧补充0
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength,int position) {
		int strLen = str.length();
		StringBuffer sb = null;
		while (strLen < strLength) {
			sb = new StringBuffer();
			if(position == 1){
				sb.append("0").append(str);// 左补0
			}else{
				sb.append(str).append("0");//右补0
			}
			str = sb.toString();
			strLen = str.length();
		}
		return str;
	}
}
