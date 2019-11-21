package com.asianwallets.common.utils;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * 
 *@category：MD5加密
 * @author RyanCai
 * @日期：2015年8月14日 上午10:10:25
 */

public class MD5 {
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * MD5加密
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(resultString.getBytes("UTF-8"));
            resultString = byteArrayToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
    
    
    /**   
     * sha256_HMAC加密    
     * @param message 消息   
     * @param secret  秘钥 
     * @return 加密后字符串  
     */  
    public static String sha256_HMAC(String message, String secret) {     
      String hash = "";      
      try {         
          Mac sha256_HMAC = Mac.getInstance("HmacSHA256");     
          SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");      
          sha256_HMAC.init(secret_key);       
          byte[] bytes = sha256_HMAC.doFinal(message.getBytes());   
          hash = byteArrayToHexString(bytes);         
          System.out.println(hash);    
          } catch (Exception e) {
          e.printStackTrace();
          System.out.println("Error HmacSHA256 ===========" + e.getMessage());   
        }       
       return hash;   
      }     

    public static void main(String[] args) {
    	System.out.println(MD5Encode("a123456"));
    	
	}
}
