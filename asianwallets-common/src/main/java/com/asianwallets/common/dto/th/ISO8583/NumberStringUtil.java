package com.asianwallets.common.dto.th.ISO8583;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-08 10:53
 **/

import java.io.ByteArrayOutputStream;

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

    /**
     * 将String转成BCD码
     *
     * @param s
     * @return
     */
    public static byte[] StrToBCDBytes(String s) {

        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    /**
     * 将BCD码转成int
     *
     * @param b
     * @return
     */
    public static int bcdToint(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int h = ((b[i] & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (b[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return Integer.parseInt(sb.toString());
    }

    /**
     * 将ASCII码转成String
     *
     * @param
     * @return
     */
    public static String asciiToString(String value)
    {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }
    /**
     * 将String码转成ASCII
     *
     * @param
     * @return
     */
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

}
