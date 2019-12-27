package com.asianwallets.common.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共使用的工具类，大家可以在这个类中添加新的方法
 *
 * @author RyanCai
 */
public class ComTools {

	/**
	 * @param datastr    :时间字符串
	 * @param dataFomart ：日期格式(如：yyyy-MM-dd HH:mm:ss)
	 * @return ：返回格式化后的日期
	 */
	public static Date fomartDate(String datastr, String dataFomart) {
		Date date = null;
		try {
			if (datastr != null && dataFomart != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(dataFomart);
				date = sdf.parse(datastr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}


	public static String fomartDate(Date date, String dataFomart) {
		String datestr = null;
		try {
			if (date != null && dataFomart != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(dataFomart);
				datestr = sdf.format(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datestr;
	}

	/**
	 * @return
	 * @category：获得商户流水号，生成规则：商户流水号 MC+yyyyMMddHHmmssfff+Random(5位数字)
	 * @author RyanCai
	 * @Time :2014年11月26日 下午3:35:51
	 */
	public static String getGuidMerchant() {
		String ds = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Random ran = new Random();
		int num = ran.nextInt(10000) + 10000;
		ds = "MC" + sdf.format(new Date()) + num;
		return ds;
	}

	/**
	 * @param mId ：当前最大的商户编号
	 * @return ：下一个商户编号
	 * @category：根据当前的最大的商户编号生成下一个商户编号
	 * @author RyanCai
	 * @Time :2014年11月27日 上午11:39:20
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
	 * @param type ：1表示的是商户，2表示的是商户用户，3表示的是商户角色组 4表示的是商户用户角色关系表，5表示的是商户费用表
	 * @return
	 * @category：安装数据库的规则生成主键GUID字符串
	 * @author RyanCai
	 * @Time :2014年11月28日 上午10:09:10
	 */
	public static String getGuid(int type) {
		String ds = null;
		if (type > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Random ran = new Random();
			int num = ran.nextInt(10000) + 10000;
			switch (type) {
				// type=1表示的是商户
				case 1:
					ds = "MC" + sdf.format(new Date()) + num;
					break;
				// type=2表示的是商户用户
				case 2:
					ds = "MU" + sdf.format(new Date()) + num;
					break;
				// type=3表示的是商户角色组
				case 3:
					ds = "MG" + sdf.format(new Date()) + num;
					break;
				// type=4表示的是商户用户角色关系表
				case 4:
					ds = "UG" + sdf.format(new Date()) + num;
					break;
				// type=5表示的是商户费用表
				case 5:
					ds = "MF" + sdf.format(new Date()) + num;
					break;
			}
		}
		return ds;
	}

	/**
	 * @param length
	 * @return
	 * @category：生成随机字符串必须包含至少一个大写，一个小写，一个数字总共8位
	 * @author RyanCai
	 * @Time :2014年12月16日 下午5:13:15
	 */
	public static String getRandomPwd() {
		String strx = "abcdefghijklmnopqrstuvwxyz";
		String strd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String strs = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 2; i++) {
			int num1 = random.nextInt(26);
			int num2 = random.nextInt(26);
			int num3 = random.nextInt(10);
			sb.append(strx.charAt(num1) + "" + strd.charAt(num2) + ""
					+ strs.charAt(num3));
		}
		for (int i = 0; i < 1; i++) {
			int num1 = random.nextInt(26);
			int num2 = random.nextInt(26);
			sb.append(strx.charAt(num1) + "" + strd.charAt(num2));
		}
		return sb.toString();
	}

	/**
	 * @param f ：需要处理的浮点数
	 * @return 返回处理后的浮点数
	 * @category：保留浮点数2位数的小数，只要后面有数就往第二位小数上进1
	 * @author RyanCai
	 * @Time :2015年10月21日 下午3:30:11
	 */
	public static double getDoublesData(double f) {
		BigDecimal b = new BigDecimal(f + 0.01);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		// System.out.println(f1);
		return f1;
	}


	public static double getDoublesData1(double f) {
		BigDecimal b = new BigDecimal(f + 0.01);
		double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		// System.out.println(f1);
		return f1;
	}

	/**
	 * 验证不能有中文，返回true表示没有中文
	 *
	 * @param str
	 * @return
	 */
	public static boolean isExitChinese(String str) {
		boolean flag = false;
		try {
			String rex = "^[^\u4e00-\u9fa5]{0,}$";
			Pattern pattern = Pattern.compile(rex);
			flag = pattern.matcher(str.trim()).matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	/**
	 * @param response
	 * @return Map<String, String>
	 * @category：将请求返回的信息格式化成map
	 * @example:TransactionType=REFUND&ServiceID=PAY&PymtMethod=CC&PaymentID=20170122104640&Amount=100.00
	 * @author linxiaowei
	 * @Time :2017-1-22
	 */
	public static Map<String, String> getResponseBodyParam(String response) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isNotBlank(response)) {
			String[] str1 = response.split("&");
			if (str1 != null && str1.length > 0) {
				for (String str2 : str1) {
					if (str2.contains("=")) {
						String[] str3 = str2.split("=");
						if (str3 != null && str3.length > 1) {
							map.put(str3[0], str3[1]);
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 转码为字符串
	 *
	 * @param inputStream
	 * @param encode
	 * @return
	 * @throws Exception
	 */
	public static String changeInputStream(InputStream inputStream,
										   String encode) throws Exception {
		// 内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = null;
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				inputStream.close();
			}
		}
		return result;
	}


	/**
	 * 参数范围校验
	 *
	 * @param parameter
	 * @param parameters
	 * @return
	 */
	public static boolean parameterCheck(String parameter, String[] parameters) {
		boolean flag = false;
		if (null != parameters && parameters.length > 0 && null != parameter) {
			for (String s : parameters) {
				if (s.equals(parameter)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 验证是>0数字 ，并且小数点后只能保留二位
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		try {
			if (StringUtils.isEmpty(str) || Double.parseDouble(str) <= 0) {
				return false;
			}
			Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 验证是>0数字 ，并且小数点后只能保留二位
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumericZero(String str) {
		try {
			if (StringUtils.isEmpty(str) || Double.parseDouble(str) < 0) {
				return false;
			}
			Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差
	 */

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 两个参数的积
	 */

	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}


	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v     需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */

	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
	 * <p>
	 * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
	 * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
	 * <p>
	 * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
	 * 192.168.1.100
	 * <p>
	 * 用户真实IP为： 192.168.1.110
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取当前时间前3分钟
	 *
	 * @param minu
	 * @return
	 */
	public static Date getLastNminTime(int minu) {
		Calendar beforeTime = Calendar.getInstance();
		beforeTime.add(Calendar.MINUTE, -minu);// N分钟之前的时间
		Date beforeD = beforeTime.getTime();
		return beforeD;
	}

	public static void main(String[] args) {

		System.out.println(getLastNminTime(10));

	}
}
