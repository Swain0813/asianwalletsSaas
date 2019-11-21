package com.asianwallets.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * UUID帮助类,系统中生成数据库编号的类
 * 
 * @author RyanCai
 * @date 2014.7.8
 * */

public class UUIDHelper {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private static Random random = new Random();

	/**
	 * 获取UUID字符串
	 * */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}

	public static String getUUID(String guidType) {
		return guidType +dateFormat.format(new Date())+ RandomFiveString();
	}

	private static String RandomFiveString() {
		return "" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
	}
	
	/**
	 * 生成主键id 格式为yyyyMMddHHmmssfff+Random(5位数字)
	 */
	public static String getSystemID(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");//只需改变输出格式
		String date = sdf.format(new Date());
		int n=0;
		n=(int)(Math.random()*100000);
		while(n<10000 || !handle(n)){
		n=(int)(Math.random()*100000);
		}
		return date+n;
	}
	
	public static boolean handle(int n){
		int[] list=new int[5];
		for(int i=0;i<5;i++){
		list[i]=n%10;
		n=n/10;
		}
		for(int i=0;i<5;i++){
		for(int j=i+1;j<5;j++){
		if(list[i]==list[j]) return false;
		}
		}
		return true;
	}
	
	public static String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");//只需改变输出格式
		String date = sdf.format(new Date());
		return date;
	}
	
	/**
	 * 
	 * @category：生成任意位数的随机数（其中只能包含：数子，大小写字母），
	 * 这里我主要是生成商户的证书128位随机数
	 *@author RyanCai
	 *@Time :2014年12月4日 下午3:37:31
	 * @param length ：随机数长度
	 * @return
	 */
	public static String getRandomString(int length){  
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  
        Random random=new Random();  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<length;i++){  
            int number =random.nextInt(62);  
            sb.append(str.charAt(number));  
        }  
        return sb.toString();  
    }
	
	/**
	 * 
	 * @category：生成任意位数的随机数（其中只能包含：数子）
	 *@author RyanCai
	 *@Time :2015年9月6日 下午12:01:45
	 * @param length
	 * @return
	 */
	public static String getRandomInt(int length){  
        String str="0123456789";  
        Random random=new Random();  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<length;i++){  
            int number =random.nextInt(10);  
            sb.append(str.charAt(number));  
        }  
        return sb.toString();  
    }
	
	/**
	 * 
	 * @category：根据当前的最大的商户编号生成下一个商户编号
	 * @author RyanCai
	 * @Time :2014年11月27日 上午11:39:20
	 * @param mId
	 *            ：当前最大的商户编号
	 * @return ：下一个商户编号
	 */
	public static String getNextMerchantID(String mId) {
		String nextId = null;
		if (mId != null && mId.length() == 12) {
			int a = Integer.parseInt(mId.substring(1));
			int b = a + 1;
			nextId = "E+" + b;
			int num = 12 - nextId.length() + 1;
			String nustr = "";
			for (int i = 0; i < num; i++) {
				nustr += "0";
			}
			if (!nustr.equals("")) {
				nextId = nextId.replace("+", nustr);
			}
		}
		return nextId;
	}
	
	/**
	 * 将微信请求参数map转换成微信需要的xml字符串
	 * @param map
	 * @return
	 */
	public static String map2XMLForWeChat(Map<String, String> map) {
		String str = null;
		if (map!=null) {
			 StringBuffer sb = new StringBuffer();  
		        sb.append("<xml>");  
		        //循环处理map中的参数
		        Set set = map.keySet();  
		        for (Iterator it = set.iterator(); it.hasNext();) {  
		            String key = (String) it.next();  
		            String value = map.get(key);  
		            sb.append("<"+key+">"+value+"</"+key+">");  
		            }  
		        sb.append("</xml>");  
		        str = sb.toString();  
		}
		//System.out.println("XML-----------"+str);
		return str;
	}

	
	/**
	 * 生成随机批次号
	 * @return
	 */
    public static String RandomBatchNum()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String format = dateFormat.format(new Date());
         int max=24;
         int min=3;
         Random random = new Random();
          int s = random.nextInt(max)%(max-min+1) + min;
          StringBuffer buffer =new StringBuffer();
          for(int i=0;i<s;i++)
          {
              Integer val = (int)(Math.random()*9+1);
              buffer.append(val.toString());
          }
        return format+buffer.toString();
    }

	public static Map<String, String> xml2MapForWeChat(String xmlstr) {
		Map<String, String> map = new HashMap();
		if (xmlstr != null && !xmlstr.equals("")) {
			try {
				Document doc = DocumentHelper.parseText(xmlstr);
				Element root = doc.getRootElement();
				List children = root.elements();
				if (children != null && children.size() > 0) {
					for(int i = 0; i < children.size(); ++i) {
						Element child = (Element)children.get(i);
						//System.out.println(child.getName() + "/" + child.getTextTrim());
						map.put(child.getName(), child.getTextTrim());
					}
				}
			} catch (Exception var7) {
				var7.printStackTrace();
			}
		}

		return map;
	}
	
	public static void main(String [] args){
		System.out.println(getSystemID());
	}
	
}
