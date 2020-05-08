package com.asianwallets.common.dto.th.demo1;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:53
 **/
/**
 * 字符工具类
 */
public class NumberStringUtil {

    public NumberStringUtil() {
    }

    /**
     * 2进制字符串转16进制字符串
     * @param binaryStr
     * @return
     */
    public static String binaryToHexString(String binaryStr){

        StringBuffer bf = new StringBuffer();
        String hexString;
        Integer temInt;
        // 每四位2进制，对应一位16进制，并从小位开始计算
        for(int i= binaryStr.length(); i>0; i=i-4){
            temInt = Integer.valueOf(binaryStr.substring(i-4<0?0:i-4,i), 2);
            hexString = Integer.toHexString(temInt);
            bf.insert(0, hexString);
        }

        return bf.toString();
    }

    /**
     * 16进制字符串转2进制字符串
     * @param hexString
     * @return
     */
    public static String hexToBinaryString(String hexString){

        StringBuffer bf = new StringBuffer();
        String[] hexValues = hexString.split("");
        String binaryString;
        // 每一位16进制，对应四位2进制
        for(String hexValue : hexValues){
            binaryString = Integer.toBinaryString(Integer.valueOf(hexValue,16));
            // 如果不足4位，左补0
            binaryString = addLeftChar(binaryString,4,'0');
            bf.append(binaryString);
        }

        return bf.toString();
    }

    /**
     * 左补字符至指定长度
     * @param str 原字符串
     * @param length 需要补到的长度
     * @param c 补位的字节
     * @return
     */
    public static String addLeftChar(String str, int length, char c) {
        str = (str == null) ? "" : str;

        StringBuilder sb = new StringBuilder(str);
        int num = length - str.length();
        for (int i = 0; i < num; i = i + 1) {
            sb.insert(0, c);
        }
        return sb.toString();
    }

    /**
     * 右补字符至指定长度
     * @param str 原字符串
     * @param length 需要补到的长度
     * @param c 补位的字节
     * @return
     */
    public static String addRightChar(String str, int length, char c) {
        str = (str == null) ? "" : str;

        StringBuilder sb = new StringBuilder(str);
        int num = length - str.length();
        for (int i = 0; i < num; i = i + 1) {
            sb.append(c);
        }
        return sb.toString();
    }

}
