package com.asianwallets.clearing.utils;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统公共的浮点数处理工具类
 * @author  : RyanCai
 * @version : v1.0.0
 * @date    : 2017-12-25
 * @see     : 蔡云龙新建此类，主要是处理系统公共浮点数处理，后面参照前面的方法要求自行添加
 *
 */
@Slf4j
public class ComDoubleUtil {
	
	/*
	 * 日志
	 */
	/**
	 * 格式化double为String，带固定位数的小数
	 * @param in
	 * @param scale
	 * @return
	 */
	public static String formatD2S(double in,int scale){
		log.info("into com.cbpay.frame.util.ComDoubleUtil.formatD2S");
		//返回字符串
		String str=null;
		//精度规则字符串
		StringBuffer scaleStr=new StringBuffer();;
		scaleStr.append("###0");//初始化
		if(scale>=0){
			if(scale>0){
				scaleStr.append(".");
				for(int i=0;i<scale;i++){
					scaleStr.append("0");
				}
			}
			System.out.println("scaleStr.toString()="+scaleStr.toString());
			DecimalFormat decimalFormat = new DecimalFormat(scaleStr.toString());//格式化设置###0.00
			str=decimalFormat.format(in);
		}
		log.info("out com.cbpay.frame.util.ComDoubleUtil.formatD2S");
		return str;
	}
	
	
	/**  
	* 提供精确的加法运算。  
	* @param v1 被加数  
	* @param v2 加数  
	* @return 两个参数的和  
	*/  
	public static double add(double v1,double v2){   
	BigDecimal b1 = new BigDecimal(Double.toString(v1));   
	BigDecimal b2 = new BigDecimal(Double.toString(v2));   
	return b1.add(b2).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();   
	}   
	
	/**
	 * 自定义保留位数，加法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double addBySize(double v1,double v2,int size){   
		BigDecimal b1 = new BigDecimal(Double.toString(v1));   
		BigDecimal b2 = new BigDecimal(Double.toString(v2));   
		return b1.add(b2).setScale(size,BigDecimal.ROUND_HALF_UP).doubleValue();   
	} 
	/**
	 * 减法
	 * @param v1
	 * @param v2
	 * @param size
	 * @return
	 */
	public static double subBySize(double v1,double v2,int size){   
		BigDecimal b1 = new BigDecimal(Double.toString(v1));   
		BigDecimal b2 = new BigDecimal(Double.toString(v2));   
		return b1.subtract(b2).setScale(size,BigDecimal.ROUND_HALF_UP).doubleValue();   
	} 
	
	/**
	 * 乘法
	 * @param v1
	 * @param v2
	 * @param size
	 * @return
	 */
	public static double mulBySize(double v1,double v2,int size){   
		BigDecimal b1 = new BigDecimal(Double.toString(v1));   
		BigDecimal b2 = new BigDecimal(Double.toString(v2));   
		return b1.multiply(b2).setScale(size,BigDecimal.ROUND_HALF_UP).doubleValue();   
	}
	
	/***
	 * 除法
	 * @param v1
	 * @param v2
	 * @param size
	 * @return
	 */
	public static double divBySize(double v1,double v2,int size){   
		if(size<0){   
		throw new IllegalArgumentException(   
		"scale参数只能是整数");   
		}   
		BigDecimal b1 = new BigDecimal(Double.toString(v1));   
		BigDecimal b2 = new BigDecimal(Double.toString(v2));   
		return b1.divide(b2,size,BigDecimal.ROUND_HALF_UP).doubleValue();   
		}
	
	
	/**  
	* 提供精确的减法运算。  
	* @param v1 被减数  
	* @param v2 减数  
	* @return 两个参数的差  
	*/  
	public static double sub(double v1,double v2){   
	BigDecimal b1 = new BigDecimal(Double.toString(v1));   
	BigDecimal b2 = new BigDecimal(Double.toString(v2));   
	return b1.subtract(b2).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();   
	} 
	
	
	
	
	/**  
	* 提供精确的乘法运算。  
	* @param v1 被乘数  
	* @param v2 乘数  
	* @return 两个参数的积  
	*/  
	public static double mul(double v1,double v2,int scale){   
	BigDecimal b1 = new BigDecimal(Double.toString(v1));   
	BigDecimal b2 = new BigDecimal(Double.toString(v2));   
	return b1.multiply(b2).setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
	} 
	
	
	
	/**  
	* 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指  
	* 定精度，以后的数字四舍五入。  
	* @param v1 被除数  
	* @param v2 除数  
	* @param scale 表示表示需要精确到小数点以后几位。  
	* @return 两个参数的商  
	*/  
	public static double div(double v1,double v2,int scale){   
	if(scale<0){   
	throw new IllegalArgumentException(   
	"scale参数只能是整数");   
	}   
	BigDecimal b1 = new BigDecimal(Double.toString(v1));   
	BigDecimal b2 = new BigDecimal(Double.toString(v2));   
	return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
	}

	
	
	
	/**
	 * 将金额转换成以分为单位的整数,分以后的忽略不计,不合法返回-1
	 * @return int 整数
	 */
	public static int formartAmtDouble2Number(double amt){
	log.info("int com.cbpay.frame.util.ComDoubleUtil.formartAmtDouble2Number#amt="+amt);
	int a=-1;
	if(amt>=0){
	double amt1=amt*100;
	DecimalFormat   df= new DecimalFormat("##0");
	String amt2=df.format(amt1);
	a=Integer.parseInt(amt2);
	}else{
	log.info("int com.cbpay.frame.util.ComDoubleUtil.formartAmtDouble2Number#输入参数小于0不合法");	
	}
	log.info("int com.cbpay.frame.util.ComDoubleUtil.formartAmtDouble2Number#a="+a);
	return a;
	}
	
	/**
	 * 判断金额和精确位数是否匹配 如amount=20.13 preNum = 2则匹配
	 * @param amount
	 * @param preNum
	 * @return
	 */
	public static boolean isAmountMatch(String amount,int preNum){
		//不保留小数,整数都让通过
		double tempAmt = Double.parseDouble(amount);
		if(tempAmt%1==0){ //为整数
			return true;
		}else{
			//截取小数部分
			String xs = amount.substring(amount.indexOf(".")+1, amount.length());
			if(!"".equals(xs) && null != xs){
				if(xs.length() <= preNum){
					return true;
				}
			}else{
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * 删除尾部的0
	 * @param str
	 * @return
	 */
    public static String removeTail0(String str){  
//      如果字符串尾部不为0，返回字符串  
        if(!str.substring(str.length() -1).equals("0")){  
            return str;  
        }else{  
//          否则将字符串尾部删除一位再进行递归  
            return removeTail0(str.substring(0, str.length() -1 ));  
        }  
    }  
    
    /**
     * 根据规则计算金额
     * @param amount
     * @param preNum 保留小数位数 0,1,2
     * @param calMethod 1四舍五入 2 全舍，3全入
     * @return
     */
    public static double getAmountByFormat(double amount,int preNum,int calMethod){
    	double resultAmt = 0d;
    	BigDecimal   b   =   new   BigDecimal(amount); 
    	//1表示四舍五入
    	if(calMethod == 1){
    		resultAmt = b.setScale(preNum,BigDecimal.ROUND_HALF_UP).doubleValue();
    	}else if(calMethod == 2){
    		//2表示全舍
    		resultAmt = b.setScale(preNum,BigDecimal.ROUND_DOWN).doubleValue();
    	}else if(calMethod == 3){
    		//3表示全入
    		resultAmt = b.setScale(preNum,BigDecimal.ROUND_UP).doubleValue();
    	}
    	
    	return resultAmt;
    }
    
    //金额验证  
	public static boolean isAmount(String str){   
	     Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$"); // 判断小数点后2位的数字的正则表达式  
	     Matcher match=pattern.matcher(str);   
	     if(match.matches()==false){   
	        return false;   
	     }else{   
	        return true;   
	     }   
	 }
	
	
	public static void main(String[] args) {
//		String s=formatD2S(0,3);
//		System.out.println(s);
//		System.out.println(mul(6.8, 0.001));
//		System.out.println(mul(0.01, 4.9));
		System.out.println(isAmount(""));
		System.out.println(addBySize(629.60000,-629.6,2));
		System.out.println(subBySize(0.0,0.0,2));

	}

}
