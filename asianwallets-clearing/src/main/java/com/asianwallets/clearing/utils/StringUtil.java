package com.asianwallets.clearing.utils;
import com.asianwallets.clearing.constant.Const;
import lombok.extern.slf4j.Slf4j;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 字符串工具类
 * </pre>
 * 
 *
 */
@Slf4j
public final class StringUtil {
	

	private static final int byteMaxhex = 255;
	private static final Pattern regInteger = Pattern.compile("\\d+");
	private static final Pattern isNumber = Pattern.compile("-{0,1}[0-9]+[.]{0,1}[0-9]*");
	private static final Pattern isLowcase = Pattern.compile("[a-z]+");
	private static final Pattern isUppercase = Pattern.compile("[A-Z]+");
	private static final char[] charArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * 字符串是否是数字
	 * 
	 * @since 1.0
	 * @param ch
	 *            字符
	 * @return boolean true:字符是数字 false:字符不是数字
	 */
	public static boolean isDigit(char ch) {
		int temp = ch & byteMaxhex;
		return temp >= 48 && temp <= 57;
	}

	/**
	 * 字符串是否是数字串
	 * 
	 * @since 1.0
	 * @param string
	 *            字符串
	 * @return boolean true:数字 false：非数字
	 */
	public static boolean isDigit(String string) {
		boolean bool = false;
		if (!isEmpty(string)) {
			Matcher matcher = regInteger.matcher(string);
			bool = matcher.matches();
		}
		return bool;
	}

	/**
	 * 字符是否是小写
	 * 
	 * @since 1.0
	 * @param ch
	 *            字符
	 * @return boolean true:小写字符 false:大写字符
	 */
	public static boolean isLowerCase(char ch) {
		int temp = ch & byteMaxhex;
		return temp >= 97 && temp <= 122;
	}

	/**
	 * 字符串是否是小写字符
	 * 
	 * @since 1.0
	 * @param string
	 *            字符串
	 * @return boolean true:字符串为小写字符串 false:字符串为大写字符串
	 */
	public static boolean isLowerCase(String string) {
		Matcher matcher = isLowcase.matcher(string);
		return matcher.matches();
	}

	/**
	 * 字符是否是大写字符
	 * 
	 * @since 1.0
	 * @param ch
	 *            字符
	 * @return boolean true:大写字符 false:小写字符
	 */
	public static boolean isUpperCase(char ch) {
		int temp = ch & byteMaxhex;
		return temp >= 65 && temp <= 90;
	}

	/**
	 * 字符串是否是大写字符串
	 * 
	 * @since 1.0
	 * @param string
	 *            字符串
	 * @return boolean true:字符串为大写字符串 false:小写字符串
	 */
	public static boolean isUpperCase(String string) {
		Matcher matcher = isUppercase.matcher(string);
		return matcher.matches();
	}

	/**
	 * 转换为无符号字符串
	 * 
	 * @param i
	 * @param shift
	 * @return
	 * @see com.cttsp.frame.util.StringUtil#toUnsignedString
	 */
	private static String toUnsignedString(long i, int shift) {
		char[] chs = new char[64];
		int charPosition = 64;
		int radix = 1 << shift;
		int value = radix - 1;
		do {
			chs[--charPosition] = charArray[(int) (i & value)];
			i >>>= shift;
		} while (i > 0);
		return new String(chs, charPosition, 64 - charPosition);
	}

	/**
	 * int值转换为十六进制
	 * 
	 * @since 1.0
	 * @param i
	 *            int值
	 * @return String 十六进制字符串
	 */
	public static String toHexString(int i) {
		return toUnsignedString(i, 4);
	}

	/**
	 * long值转换为十六进制
	 * 
	 * @since 1.0
	 * @param i
	 *            long值
	 * @return String 十六进制字符串
	 */
	public static String toHexString(long i) {
		return toUnsignedString(i, 4);
	}

	/**
	 * int值转换为八进制
	 * 
	 * @since 1.0
	 * @param i
	 *            int值
	 * @return String 八进制字符串
	 */
	public static String toOctalString(int i) {
		return toUnsignedString(i, 3);
	}

	/**
	 * long值转换为八进制
	 * 
	 * @since 1.0
	 * @param i
	 *            long值
	 * @return String 八进制字符串
	 */
	public static String toOctalString(long i) {
		return toUnsignedString(i, 3);
	}

	/**
	 * int值转换为二进制
	 * 
	 * @since 1.0
	 * @param i
	 *            int值
	 * @return String 二进制字符串
	 */
	public static String toBinaryString(int i) {
		return toUnsignedString(i, 1);
	}

	/**
	 * long值转换为二进制
	 * 
	 * @since 1.0
	 * @param i
	 *            long值
	 * @return String 二进制字符串
	 */
	public static String toBinaryString(long i) {
		return toUnsignedString(i, 1);
	}

	/**
	 * 判断对象是否为Null
	 * 
	 * @author admin
	 * @param obj
	 * @return
	 * @see com.cttsp.frame.util.StringUtil#isNull
	 */
	public static boolean isNull(Object obj) {
		return null == obj;
	}

	/**
	 * 判断对象是否为空或空字符串
	 * 
	 * @author admin
	 * @param obj
	 * @return boolean (true:空，false:非空)
	 * @see com.cttsp.frame.util.StringUtil#isEmpty
	 */
	
	
	public static boolean isEmpty(Object obj) {
		boolean bool = true;
		if (null != obj) {
			if (obj instanceof String) {
				if (!"".equals(obj.toString().trim()) && !"null".equals(obj.toString().trim())) {
					bool = false;
				}
			} else {
				bool = false;
			}
		}
		return bool;
	}

	/**
	 * 产生唯一ID
	 * 
	 * @param param
	 * @return
	 */
	public static String getUniqueId(String param) {
		if (null != param) {
			param = param + System.currentTimeMillis();
		}
		return param;
	}

	/**
	 * 处理Null或空字符串
	 * 
	 * @author admin
	 * @param string
	 * @return String
	 * @see com.cttsp.frame.util.StringUtil#nullToString
	 */
	public static String excNullToString(String string) {
		return excNullToString(string, Const.Code.EMPTY_STRING);
	}

	/**
	 * 处理Null或空字符串
	 * 
	 * @author admin
	 * @param string
	 * @param added
	 * @return String
	 * @see com.cttsp.frame.util.StringUtil#nullToString
	 */
	public static String excNullToString(String string, String added) {
		if (isNull(string)) {
			string = added;
		}
		return string;
	}

	/**
	 * 处理Null或空对象
	 * 
	 * @author admin
	 * @return Object
	 */
	public static Object excNullToObject(Object obj) {
		return excNullToObject(obj, new Object());
	}

	/**
	 * 处理Null或空对象
	 * 
	 * @author admin
	 *            obj
	 *            added
	 * @return Object
	 */
	public static Object excNullToObject(Object obj, Object added) {
		if (isNull(obj)) {
			obj = added;
		}
		return obj;
	}

	/**
	 * 将字符串转换成整数类型，如果为空则转换成0
	 * 
	 * @author admin
	 * @param string
	 * @return
	 */
	public static int stringToInt(String string) {
		return stringToInt(string, Const.Code.NUM_0);
	}

	/**
	 * 将字符串转换成整数类型，如果为空则转换成指定值
	 * 
	 * @author admin
	 * @param string
	 * @return
	 */
	public static int stringToInt(String string, int added) {
		int result = 0;
		try {
			if(string != null){
				result = Integer.parseInt(string.trim());
			} else {
				result = added;
			}
		} catch (Exception e) {
			result = added;
		}
		return result;
	}

	/**
	 * 将字符串转换成整数类型，如果为空则转换成0
	 * 
	 * @author admin
	 * @param string
	 * @return
	 */
	public static long stringToLong(String string) {
		return stringToLong(string, Const.Code.NUM_0);
	}

	/**
	 * 将字符串转换成整数类型，如果为空则转换成指定值
	 * 
	 * @author admin
	 * @param string
	 * @return
	 */
	public static long stringToLong(String string, long added) {
		long result = 0;
		try {
			if(string != null){
				result = Long.parseLong(string.trim());
			} else {
				result = added;
			}
		} catch (Exception e) {
			result = added;
		}
		return result;
	}

	/**
	 * 将字符串转换成float类型
	 * 
	 * @param string
	 * @return float
	 */
	public static float stringToFloat(String string) {
		return stringToFloat(string, 0.0f);
	}

	/**
	 * 将字符串转换成float类型,如果为空则转为指定的值
	 * 
	 * @param string
	 * @return float
	 */
	public static float stringToFloat(String string, float added) {
		float result = 0.0f;
		try {
			if(string != null){
				result = Float.parseFloat(string.trim());
			} else {
				result = added;
			}
		} catch (Exception e) {
			result = added;
		}
		return result;
	}

	/**
	 * 将字符串转换成double类型,如果为空则转为指定的值
	 * 
	 * @param string
	 * @return double
	 */
	public static double stringToDouble(String string) {
		return stringToDouble(string, 0.0d);
	}

	/**
	 * 将字符串转换成double类型,如果为空则转为指定的值
	 * 
	 * @param string
	 * @return double
	 */
	public static double stringToDouble(String string, double added) {
		double result = 0.0d;
		try {
			if(string != null){
				result = Double.parseDouble(string.trim());
			} else {
				result = added;
			}
		} catch (Exception e) {
			result = added;
		}
		return result;
	}

	/**
	 * 判断是否为数值类型（整数、小数、负数）
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNumbers(String string) {
		boolean bool = false;
		try {
			Matcher matcher = isNumber.matcher(string);
			bool = matcher.matches();
		} catch (Exception e) {
			log.error("参数字符串为空！", e);
		}
		return bool;
	}

	/**
	 * 将Unicode编码转换为正常字符
	 * 
	 * @param param
	 * @return
	 */
	public static String stringUncode(String param) {
		if (param != null && !param.trim().equals("")) {
			try {
				param = URLDecoder.decode(param, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return param;
	}

	/**
	 * 将字符转换为Unicode编码
	 * 
	 * @param param
	 * @return
	 */
	public static String stringEncode(String param) {
		if (param != null && !param.trim().equals("")) {
			try {
				param = URLEncoder.encode(param, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return param;
	}

	/**
	 * 
	 * @简述：<将Object对象转换为boolean 判断是否为空，为空在则返回false>
	 * @详述：<详细介绍> boolean
	 * @param str
	 * @return
	 * 
	 */
	public static boolean parseBoolean(String str) {
		boolean bool = false;
		if (str != null && !"".equals(str.trim())) {
			bool = Boolean.parseBoolean(str);
		}
		return bool;
	}

	/**
	 * 
	 * @简述：<将Object对象转换为boolean 判断是否为空，为空在则返回false>
	 * @详述：<详细介绍> boolean
	 * @return
	 * 
	 */
	public static boolean parseBoolean(Object obj) {
		boolean bool = false;
		if (obj != null) {
			bool = parseBoolean(obj.toString());
		}
		return bool;
	}

	/**
	 * 
	 * @简述：<将参数转换为UTF-8编码>
	 * @详述：<根据不同浏览器处理方式不一样>
	 * @param param
	 * @return
	 * 
	 */
	public static String toUtf8(String param) {
		try {
			param = new String(param.getBytes("UTF-8"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return param;
	}

	/**
	 * 
	 * @简述：<将参数转换为GBK编码>
	 * @详述：<根据不同浏览器处理方式不一样>
	 * @param param
	 * @return
	 * 
	 */
	public static String toGbk(String param) {
		try {
			param = new String(param.getBytes("GBK"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return param;
	}

	/**
	 * 将字符串字母全部反转
	 * 
	 *            字符串
	 * @return String 反转后的字符串
	 */
	public static String reversal(String param) {
		if (param != null && param.length() > 1) {
			StringBuffer sb = new StringBuffer();
			String[] str = param.split("");
			for (int i = (str.length - 1); i >= 0; i--) {
				sb.append(str[i]);
			}
			param = sb.toString();
		}
		return param;
	}

	/**
	 * 
	 * @简述：将字符串中包含的回车换行符\n 替换成"< b r >"
	 * @param content
	 * @return
	 * 
	 */
	public static String replaceEnter(String content) {
		if (content != null) {
			content = content.replaceAll("\n", "<BR>");
		}
		return content;
	}

	/**
	 * 
	 * @简述：将字符串中包含的"< b r >" 替换成回车换行符\n
	 * @param content
	 * @return
	 * 
	 */
	public static String replaceBr(String content) {
		if (content != null) {
			content = content.replaceAll("<BR>", "\n");
			content = content.replaceAll("<br>", "\n");
		}
		return content;
	}

	public static String firstCharToUpperCase(String content) {
		if (!isEmpty(content)) {
			String tou = content.substring(0, 1);
			String wei = content.substring(1);
			content = tou.toUpperCase() + wei;
		}
		return content;
	}

	public static String firstCharToLowerCase(String content) {
		if (!isEmpty(content)) {
			String tou = content.substring(0, 1);
			String wei = content.substring(1);
			content = tou.toLowerCase() + wei;
		}
		return content;
	}

	/**
	 * 反格式化byte
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] hex2byte(String s) {
		byte[] src = s.toLowerCase().getBytes();
		byte[] ret = new byte[src.length / 2];
		for (int i = 0; i < src.length; i += 2) {
			byte hi = src[i];
			byte low = src[i + 1];
			hi = (byte) ((hi >= 'a' && hi <= 'f') ? 0x0a + (hi - 'a') : hi - '0');
			low = (byte) ((low >= 'a' && low <= 'f') ? 0x0a + (low - 'a') : low - '0');
			ret[i / 2] = (byte) (hi << 4 | low);
		}
		return ret;
	}

	/**
	 * 格式化byte
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] out = new char[b.length * 2];
		for (int i = 0; i < b.length; i++) {
			byte c = b[i];
			out[i * 2] = Digit[(c >>> 4) & 0X0F];
			out[i * 2 + 1] = Digit[c & 0X0F];
		}
		return new String(out);
	}

	/**
	 * 去空格、退格等<br>
	 * type:为空时去两边空格,
	 * type[0]=1:去掉所有空格，type[0]=2:只保留单空格，type[0]=3:替换退格符为单空格，type[
	 * 0]=4:去掉退格符，type[0]=5:去掉所有空格与退格
	 * 
	 * @param param
	 * @param type
	 * @return
	 */
	public static String trim(String param, int... type) {
		if (null != param) {
			if (type == null) {
				param = param.trim();
			} else if (type[0] == 1) {
				param = param.replaceAll(" ", "");
			} else if (type[0] == 2) {
				while (param.indexOf("  ") != -1) {
					param = param.replaceAll("  ", " ");
				}
			} else if (type[0] == 3) {
				while (param.indexOf("	") != -1) {
					param = param.replaceAll("	", " ");
				}
			} else if (type[0] == 4) {
				param = param.replaceAll("	", "");
			} else if (type[0] == 5) {
				param = param.replaceAll(" ", "");
				param = param.replaceAll("	", "");
			}
		}
		return param;
	}

	/**
	 * 去掉多行注释 （/ * * /）
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceNote(String str) {
		if (null != str) {
			str = str.replaceAll("\n", "≈≒");
			int start = -1, end = -1;
			while ((start = str.indexOf("/*")) != -1 && (end = str.indexOf("*/")) != -1) {
				str = str.substring(0, start) + str.substring(end + 2);
			}
			str = str.replaceAll("≈≒", "\n");
		}
		return str;
	}

	/**
	 * 
	 * 将Ascii码转中文
	 * 
	 * @author admin
	 * @param source
	 * @return
	 */
	public static String convert(String source) {
		if (null == source || " ".equals(source)) {
			return source;
		}
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < source.length()) {
			if (source.charAt(i) == '\\') {
				int j = Integer.parseInt(source.substring(i + 2, i + 6), 16);
				sb.append((char) j);
				i += 6;
			} else {
				sb.append(source.charAt(i));
				i++;
			}
		}
		return sb.toString();
	}
	
	
    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 
     * @category：生成指定位数的数字验证码
     *@author RyanCai
     *@Time :2015年10月22日 下午6:26:12
     * @param length：验证码长度
     * @return
     */
    public static String getRandomCodeByLength(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    
    /**
     * 
     * @category：生成主键ID编号
     *@author RyanCai
     *@Time :2016年1月7日 下午5:06:02
     * @param guidType：2位前缀大写字母
     * @return
     */
    public static String getUUID(String guidType) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return guidType +dateFormat.format(new Date())+ getRandomInt(5);
	}
    
    /**
     * 
     * @category：生成主键ID编号
     *@author linxiaowei
     *@Time :2016年1月7日 下午5:06:02
     * @param guidType：2位前缀大写字母,length:返回长度
     * @return 返回20位
     */
    public static String getUUIDByLength(String guidType,int length) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String str =guidType +dateFormat.format(new Date())+ getRandomInt(length);
		str=str.substring(0, length);
		return str;
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
	 * 判断字符是否为空
	 * 
	 * @param: String param
	 * @return: boolean
	 */
	public static boolean nullOrBlank(String param) {
		return (param == null || param.trim().equals("")) ? true : false;
	}
	
	
	/**
	 * 
	 * @Package :com.cbpay.frame.util.StringUtil.java
	 * @Author :Administrator
	 * @Date:2018年10月26日 下午7:01:14
	 * @Desc:获得字符串的编码格式
	 * @param :@param str
	 * @param :@return
	 * @return String    返回类型
	 */
	public static String getEncoding(String str) {    
        String encode = " ";
       try {    
    	   encode = "GB2312"; 
           if (str.equals(new String(str.getBytes(encode), encode))) {    
               String s = encode;    
               return s;    
            }
           encode = "ISO-8859-1";    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s1 = encode;    
               return s1;    
            }    
           encode = "UTF-8";    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s2 = encode;    
               return s2;    
            }    
           encode = "GBK";    
           if (str.equals(new String(str.getBytes(encode), encode))) {    
                String s3 = encode;    
               return s3;    
            }    
        } catch (Exception e) {  
        	e.printStackTrace();
        }    
       return encode;    
    }    

	

	public static void main(String[] args) {
		System.out.println(getRandomString(6));
	}
	

}
