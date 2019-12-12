package com.asianwallets.clearing.utils;

import com.asianwallets.clearing.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * <pre>
 * 日期时间工具类
 * </pre>
 * <ul>
 * <li>将时间日期字符串转换为Date类型 <br>
 * public static Date parse(String dateStr); <br>
 * public static Date parse(String dateStr, String pattern)</li>
 * <li>将Date格式化为正规日期时间字符串 <br>
 * public static String format(Date date); <br>
 * public static String format(Date date, String pattern);</li>
 * </ul>
 *
 */
@Slf4j
public final class DateUtil {
	

	/**
	 * SimpleDateFormat日期+时间格式yyyy-MM-dd HH:mm:ss
	 */
	public static SimpleDateFormat SDF_DATETIME = new SimpleDateFormat(Const.Code.PATTERN_DATE_TIME);
	/**
	 * SimpleDateFormat日期格式yyyy-MM-dd
	 */
	public static SimpleDateFormat SDF_DATE = new SimpleDateFormat(Const.Code.PATTERN_DATE);

	/**
	 * 
	 * 将时间日期字符串转换为Date类型 <br>
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parse(String dateStr) {
		return parse(dateStr, null);
	}

	/**
	 * 
	 * 将时间日期字符串转换为指定格式的Date类型 <br>
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parse(String dateStr, String pattern) {
		Date date = null;
		if (null != dateStr) {
			try {
				if (pattern != null) {
					SimpleDateFormat sim = new SimpleDateFormat(pattern);
					date = sim.parse(dateStr);
				} else {
					date = SDF_DATETIME.parse(dateStr);
				}
			} catch (ParseException e) {
				log.error("时间格式转换失败！", e);
			}
		} else {
			log.error("字符串为空：dateStr=" + dateStr);
		}
		log.debug("com.cscenter.frame.util.DateUtil.parse#输出值：date="+date);
		return date;
	}

	/**
	 * 
	 * 将时间日期字符串转换为指定格式的Date类型 <br>
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parseYX(String dateStr) {
		Date date = null;
		if (StringUtils.isNotBlank(dateStr)) {
			try {
				if (dateStr.length() < 12) {
					dateStr += " 00:00:00";
				}
				date = SDF_DATETIME.parse(dateStr);
			} catch (ParseException e) {
				log.error("parseYX 时间格式转换失败！", e);
			}
		} else {
			log.error("parseYX 字符串为空：dateStr=" + dateStr);
		}
		return date;
	}
	
	
	/**
	 * 将Date格式化为正规日期时间字符串
	 * 
	 * @author admin
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return format(date, null);
	}

	/**
	 * 将Date格式化为指定格式日期时间字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date, String pattern) {
		String dateStr = null;
		if (date != null) {
			try {
				if (pattern != null) {
					SimpleDateFormat sim = new SimpleDateFormat(pattern);
					dateStr = sim.format(date);
				} else {
					dateStr = SDF_DATETIME.format(date);
				}
			} catch (Exception e) {
				log.error("时间格式化字符串失败！", e);
			}
		} else {
			log.error("Date参数为空：date=" + date);
		}
		return dateStr;
	}

	// 将时间字符串转换成指定时间格式的时戳
	public static Timestamp parseToTimestamp(String dateStr, String pattern) {
		Timestamp tmp = null;
		// 判断字符串是否为空
		if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(pattern)) {
			throw new IllegalArgumentException("The parameter is null.");
		}
		// 构造SimpleDateFormat对象
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			tmp = new Timestamp(sdf.parse(dateStr).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tmp;
	}

	/**
	 * 将时间字符串转换成指定时间格式的时戳
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Timestamp parseToTimestamp(String dateStr) {
		return parseToTimestamp(dateStr, Const.Code.PATTERN_DATE_TIME);
	}	
	
	/*
	 *  将时间转换成指定时间格式的时戳字符串(毫秒级别字符串)
	 */
	public static String parseTimestampToStr(Date d) {
		String str = null;
		try {
		// 判断字符串是否为空
		if (!StringUtil.isEmpty(d)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(d);
			str=calendar.getTimeInMillis()+"";
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("对应毫秒：" + str);
		return str;
	}

	/**
	 * 根据出生日期计算标准年龄
	 * 
	 * @param birthDate
	 * @return
	 */
	public static int getAgeByDate(Date birthDate) {
		Date day = new Date();
		int age = day.getYear() - birthDate.getYear();
		birthDate.setYear(birthDate.getYear() + age);
		age = birthDate.getTime() - day.getTime() > 0 ? age - 1 : age;
		return age;
	}

	/**
	 * 计算当前日期在当前年份中是第几周
	 * 
	 * @return
	 */
	public static int getWeekByDate() {
		return getWeekByDate(new Date());
	}

	/**
	 * 计算指定日期在当前年份中是第几周
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeekByDate(Date date) {
		SimpleDateFormat sim = new SimpleDateFormat("w");
		String wk = sim.format(date);
		return com.asianwallets.clearing.utils.StringUtil.stringToInt(wk, -1);
	}

	/**
	 * 根据出生日期字符串计算标准年龄
	 * 
	 * @param birthDateStr
	 * @return
	 */
	public static int getAgeByDateStr(String birthDateStr) {
		Date birthDate = parse(birthDateStr, "yyyy-MM-dd");
		return getAgeByDate(birthDate);
	}

	/**
	 * 获取当前日期前后N天日期Date
	 * 
	 * @param dayNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByDayNumber(int dayNumber) {
		return getBeforeOrAfterDateByDayNumber(new Date(), dayNumber);
	}

	/**
	 * 获取指定日期前后N天日期Date
	 * 
	 * @param date
	 * @param dayNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByDayNumber(Date date, int dayNumber) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, dayNumber);
		return c.getTime();
	}

	/**
	 * 获取当前日期的前后N月的日期Date
	 * 
	 * @param monthNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByMonthNumber(int monthNumber) {
		return getBeforeOrAfterDateByMonthNumber(new Date(), monthNumber);
	}

	/**
	 * 获取指定日期前后N月的日期Date
	 * 
	 * @param date
	 * @param monthNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByMonthNumber(Date date, int monthNumber) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, monthNumber);
		return c.getTime();
	}
	
	/**
	 * 获取指定日期前后N年的日期Date
	 * 
	 * @param date
	 * @param yearNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByYearNumber(Date date, int yearNumber) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, yearNumber);
		return c.getTime();
	}
	
	/**
	 * 获取指定日期后N分钟的日期Date
	 * @param date
	 * @param minNumber
	 * @return
	 */
	public static Date getBeforeOrAfterDateByMinNumber(Date date, int minNumber) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minNumber);
		return c.getTime();
	}
	


	/**
	 * 拿date与当前日期比较大小
	 * 
	 * @param date ：需要比较的日期时间
	 * @param genre ：比较类型 （模式：传入时间 -比较类型- 当前时间 ）
	 *            比较类型 1:< , 2:> , 3: <= , 4: >= , 5: ==
	 *            1表示判断传入时间是否小于当前时间
	 * @return
	 */
	public static boolean compareDateOrTime(Date date, int genre) {
		return compareDateOrTime(date, new Date(), genre);
	}

	/**
	 * 两个日期比较大小
	 * 
	 * @param date1
	 * @param date2
	 * @param genre
	 *            比较类型 1:< , 2:> , 3: <= , 4: >= , 5: ==
	 * @return
	 */
	public static boolean compareDateOrTime(Date date1, Date date2, int genre) {
		boolean bool = false;
		long times1 = date1.getTime();
		long times2 = date2.getTime();
		if (genre == 1) {
			bool = times1 < times2;
		} else if (genre == 2) {
			bool = times1 > times2;
		} else if (genre == 3) {
			bool = times1 <= times2;
		} else if (genre == 4) {
			bool = times1 >= times2;
		} else if (genre == 5) {
			bool = times1 == times2;
		}
		return bool;
	}

	/**
	 * 拿date字符串与当前日期比较大小
	 * 
	 * @author RyanCai
	 * @date 2013-4-2 下午03:36:57
	 * @param date
	 * @param genre
	 *            比较类型 1:< , 2:> , 3: <= , 4: >= , 5: ==
	 * @return
	 */
	public static boolean compareDateOrTime(String date, int genre) {
		Date day = new Date();
		if (date != null && date.length() < 12) {
			day = parse(SDF_DATE.format(day));
		}
		return compareDateOrTime(parse(date), day, genre);
	}

	/**
	 * 两个日期字符串比较大小
	 * 
	 * @param date1
	 * @param date2
	 * @param genre
	 *            比较类型 1:< , 2:> , 3: <= , 4: >= , 5: ==
	 * @return
	 */
	public static boolean compareDateOrTime(String date1, String date2, int genre) {
		return compareDateOrTime(parse(date1), parse(date2), genre);
	}

	/**
	 * 得到当前月最后一天
	 * 
	 * @return
	 */
	public static String getLastMonDay() {
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, maxDay);
		return DateUtil.format(cal.getTime(), Const.Code.PATTERN_DATE);
	}

	/**
	 * 获得某个周的第一天
	 * 
	 * @param year
	 *            年份
	 * @param week
	 *            周
	 * @return String yyyy-MM-dd字符串
	 */
	public static String getFirstOfWeek(int year, int week) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.WEEK_OF_YEAR, week);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(c.getTime());
	}

	/**
	 * 获得某个周的最后一天
	 * 
	 * @param year
	 *            年份
	 * @param week
	 *            周
	 * @return String yyyy-MM-dd字符串
	 */
	public static String getLastOfWeek(int year, int week) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.WEEK_OF_YEAR, week);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.setFirstDayOfWeek(Calendar.SUNDAY);
		c.set(Calendar.DAY_OF_WEEK, (c.getFirstDayOfWeek() + 6));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(c.getTime());
	}

	/**
	 * 
	 * 拿时间与当前时间计算相差值
	 * 
	 *            时间1
	 * @param genre
	 *            类型 1：相差秒，2：相差分钟，3：相差小时，4：相差天数
	 * @return float
	 */
	public static float calculateTime(Date date, int... genre) {
		return calculateTime(date, new Date(), genre);
	}

	/**
	 * 
	 * 计算两个时间间的相差值
	 * 
	 * @param date1
	 *            时间1
	 * @param date2
	 *            时间2
	 * @param genre
	 *            类型 1：相差秒，2：相差分钟，3：相差小时，4：相差天数
	 * @return float
	 */
	public static float calculateTime(Date date1, Date date2, int... genre) {
		long times = date1.getTime() - date2.getTime();
		float result = 0;
		if (genre.length > 0) {
			switch (genre[0]) {
			case 1: // 秒
				result = times / (float) 1000;
				break;
			case 2:// 分钟
				result = times / (float) (1000 * 60);
				break;
			case 3:// 小时
				result = times / (float) (1000 * 60 * 60);
				break;
			case 4:// 天
				result = times / (float) (1000 * 60 * 60 * 24);
				break;
			default:// 毫秒
				result = times;
				break;
			}
		} else {
			// 毫秒
			result = times;
		}
		return result;
	}
	
	public static String convertToStrCN(String x){
       SimpleDateFormat sdf1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
       try
       {
       	   Date date=sdf1.parse(x);
           SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
           String sDate=sdf.format(date);
           return sDate;
       }
       catch (ParseException e)
       {
           e.printStackTrace();
       }
	   return "";
	}
	
	/**
	 * 当前时间T+1
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }
	
	/*
	 * 当前时间的昨天
	 */
	public static Date getYesterDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);//-1今天的时间减一天
        date = calendar.getTime();
        return date;
    }

	/**
	 * 日期增加一天
	 * @param date
	 * @return
	 */
	public static Date addDay(Date date,int num) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date); //你自己的数据进行类型转换
		calendar.add(calendar.DATE,num);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime();
		return date;
	}
	
	/**
	 * 日期转字符串
	 * @param time
	 * @param patten
	 * @return
	 */
	public static String dateToString(Date time,String patten){ 
	    SimpleDateFormat formatter; 
	    formatter = new SimpleDateFormat (patten); 
	    String ctime = formatter.format(time); 
	    return ctime; 
	} 
	
	/**
	 * 判断一个日期是星期几
	 * @return
	 * @throws Exception
	 */
	public static int dayForWeek(Date date, String patten) {
		int dayForWeek = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat(patten);
			String dateStr = format.format(date);
			Calendar c = Calendar.getInstance();
			c.setTime(format.parse(dateStr));

			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayForWeek;
	}

	
	
	/**
	 * 和当前时间比较如果为空返回当前时间，如果小于当前时间1年，则返回1年前今天,否则返回输入时间
	 * @param inputDate
	 * @return 返回不带时分秒的年月日
	 */
	public static Date getLessLastYearDate(Date inputDate){
		//获取一年前的时间
		Calendar cal=Calendar.getInstance(); 
		cal.add(Calendar.YEAR,-1);
		Date lastYeartime=cal.getTime();
		Date date=null;
		//为空返回当前时间
		if(null==inputDate){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String s = sdf.format(new Date());
			try {
				date=sdf.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("时间转换错误");
				date=null;
			}
		}else if(inputDate.getTime()<lastYeartime.getTime()){
			//小于去年的今天返回去年的今天
			//date=lastYeartime;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String s = sdf.format(lastYeartime);
			try {
				date=sdf.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("时间转换错误");
				date=null;
			}
		}else{
			date=inputDate;
		}
		return date;
	}
	
	
	/**
	 * 获取当天时间最后一秒
	 */
	public static Date getCurrentDayLastSec(Date inputDate){
		Date date=null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = sdf.format(inputDate);
			str=str.replace(str.substring(11),"23:59:59");
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("时间转换错误");
		}
		return date;
	}

	
	/**
	 * 比较两个时间是否大于一年
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isOneYear(Date start, Date end) {
        Calendar startday = Calendar.getInstance();
        Calendar endday = Calendar.getInstance();
        startday.setTime(start);
        endday.setTime(end);
        long sl = startday.getTimeInMillis();
        long el = endday.getTimeInMillis();
        long days = ((el - sl) / (1000 * 60 * 60 * 24));
        if(days<=366){
        	 return true;
        }else{
        	 return false;
        }
    }
	
	
	
	/**
	 * 主函数
	 */
	public static void main(String[] args) {
		
//		System.out.println(calculateTime(new Date(2013 - 1900, 11, 25, 14, 58, 25), 2));
//		System.out.println(Math.abs(calculateTime(new Date(2013 - 1900, 11, 25, 14, 58, 25), 2)));
		//Date sholddtime=DateUtil.parse("20170208210400", "yyyyMMddHHmmss");
//			System.out.println(dayForWeek(new Date(),"yyyy-MM-dd"));
		//比较一个传入的时间是否比当前时间大
		//Date nd=new Date();
		//Date sholddtime=DateUtil.parse("201707071250".substring(0, 8),"yyyyMMdd");
//		Date nowtime=DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
//		boolean isyes=compareDateOrTime(sholddtime,nowtime,4);
		//System.out.println(isOneYear(nd,nd));
		/**
		 * dateStr=2019-04-04 14:21:56/pattern=yyyyMMddHHmmss
           com.cscenter.frame.util.DateUtil.parse#输出值：date=Tue Dec 04 00:04:14 GMT+08:00 2018
		 */
		System.out.println("TEST01---"+DateUtil.parse("2018-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		System.out.println("TEST02---"+DateUtil.parse("20180101000000", "yyyyMMddHHmmss"));
//		String d=DateUtil.parseTimestampToStr(new Date());
		
//		Date stdate=DateUtil.parse("20180810", "yyyyMMdd");//应结时间
//		System.out.println(stdate);
		
//		Date stdate=DateUtil.getBeforeOrAfterDateByYearNumber(new Date(), 100);//应结时间
//		System.out.println(stdate);
		
		
	}
	
	


}
