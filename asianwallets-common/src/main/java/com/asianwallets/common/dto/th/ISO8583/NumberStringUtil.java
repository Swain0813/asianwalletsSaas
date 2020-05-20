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
    public static String bcd2Str(byte[] b) {
        char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder(b.length * 2);

        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[(b[i] & 240) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 15]);
        }

        return sb.toString();
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte[] abt = new byte[len];
        if (len >= 2) {
            len /= 2;
        }

        byte[] bbt = new byte[len];
        abt = asc.getBytes();

        for (int p = 0; p < asc.length() / 2; ++p) {
            int j;
            if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = abt[2 * p] - 97 + 10;
            } else if (abt[2 * p] >= 65 && abt[2 * p] <= 90) {
                j = abt[2 * p] - 65 + 10;
            } else {
                j = abt[2 * p] - 48;
            }

            int k;
            if (abt[2 * p + 1] >= 97 && abt[2 * p + 1] <= 122) {
                k = abt[2 * p + 1] - 97 + 10;
            } else if (abt[2 * p + 1] >= 65 && abt[2 * p + 1] <= 90) {
                k = abt[2 * p + 1] - 65 + 10;
            } else {
                k = abt[2 * p + 1] - 48;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static void main(String[] args) {
        String s = "5F510BB6A9B5A5B4A6C0EDD6D0025F55523035323002393136313531023135383939350232303230303532303030303030313130363130303137383434350277656978696E3A2F2F77787061792F62697A70617975726C3F70723D65614646764F7502";
        String[] split = s.split("02");
        System.out.println(split);
    }
}
