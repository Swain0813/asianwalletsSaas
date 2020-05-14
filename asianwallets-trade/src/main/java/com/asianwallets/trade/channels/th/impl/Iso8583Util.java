package com.asianwallets.trade.channels.th.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * iso8583 位图生成与解析工具
 *
 * @author Lewis
 */
public class Iso8583Util {

    /**
     * 初始64和128域
     *
     * @param d
     * @return
     * @throws Exception
     */
    public static String initBitMap(int d) throws Exception {

        StringBuffer bf = new StringBuffer();
        if (d == 64 || d == 128) {
            for (int i = 0; i < d; i++) {
                bf.append("0");
            }
            if (d == 128) {
                bf.replace(0, 1, "1");
            }
        } else {
            throw new Exception("只能是64或者是128域初始化！");
        }
        return bf.toString();
    }

    public static String bitMapFormat(int[] list) throws Exception {
        String res = "0000000000000000";
        // 默认域长度
        int defultMap = 64;
        // 注意，初始域图默认为128域
        String bitMaps = initBitMap(128);
        if (list != null) {
            for (int i : list) {
                if (i > 64) {
                    defultMap = 128;
                }
                if (i > 128) {
                    throw new Exception("不支持的域生成！");
                }
                bitMaps = change16bitMapFlag(i, bitMaps);
                res = getBitMapDataSource(defultMap, bitMaps);
            }
            if (defultMap == 64) {
                String btm64 = "0" + bitMaps.substring(0, 64).substring(1, bitMaps.substring(0, 64).length());
                bitMaps = btm64;
            }
            res = getBitMapDataSource(defultMap, bitMaps);
        }
        return res.toUpperCase();
    }

    /**
     * 改变128位图中的标志为1
     *
     * @param fieldNo
     * @param res
     * @return
     */
    public static String change16bitMapFlag(int indexNo, String res) {
        res = res.substring(0, indexNo - 1) + "1" + res.substring(indexNo);
        return res;
    }

    /**
     * 二进制字符串转十六进制
     *
     * @param c
     * @param bitMap
     * @return
     */
    public static String getBitMapDataSource(int c, String bitMap) {
        // 注意每4个二进制表示一位16进制
        int s = c / 4;
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < s; i++) {
            String two = bitMap.substring(i * 4, i * 4 + 4);

            switch (two) {
                case "0000":
                    bf.append("0");
                    break;

                case "0001":
                    bf.append("1");
                    break;

                case "0010":
                    bf.append("2");
                    break;

                case "0011":
                    bf.append("3");
                    break;

                case "0100":
                    bf.append("4");
                    break;

                case "0101":
                    bf.append("5");
                    break;

                case "0110":
                    bf.append("6");
                    break;

                case "0111":
                    bf.append("7");
                    break;

                case "1000":
                    bf.append("8");
                    break;

                case "1001":
                    bf.append("9");
                    break;

                case "1010":
                    bf.append("a");
                    break;

                case "1011":
                    bf.append("b");
                    break;

                case "1100":
                    bf.append("c");
                    break;

                case "1101":
                    bf.append("d");
                    break;

                case "1110":
                    bf.append("e");
                    break;

                case "1111":
                    bf.append("f");
                    break;
            }
        }
        return bf.toString();
    }


    /**
     * 根据bitMap解析具体的域
     *
     * @param bitMapSource
     * @return
     */
    public static List<Integer> getBitMapNum(String bitMapSource) {
        if (bitMapSource.contains(" ")) {
            bitMapSource = bitMapSource.replaceAll(" ", "");
        }
        List<Integer> list = new ArrayList<Integer>();
        //先转16位二进制
        String bits = hexStrToBinaryStr(bitMapSource);
        char[] strChar = bits.toCharArray();

        for (int i = 0; i < strChar.length; i++) {
            String s = String.valueOf(strChar[i]);
            if (s.equals("1")) {
                list.add(i + 1);
            }
        }
        return list;
    }


    /**
     * 将十六进制的字符串转换成二进制的字符串
     * 适用于转换bitMap
     *
     * @param hexString
     * @return
     */
    public static String hexStrToBinaryStr(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        // 将每一个十六进制字符分别转换成一个四位的二进制字符
        for (int i = 0; i < hexString.length(); i++) {
            String indexStr = hexString.substring(i, i + 1);
            String binaryStr = Integer.toBinaryString(Integer.parseInt(indexStr, 16));
            while (binaryStr.length() < 4) {
                binaryStr = "0" + binaryStr;
            }
            sb.append(binaryStr);
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("64位图初始:" + initBitMap(64));
        System.out.println("128位图初始:" + initBitMap(128));
        //假设要在12467域填充数据
        int[] l = {11, 32, 41, 42, 60, 63};
        System.out.println("生成64域位图：" + bitMapFormat(l));
        //如果某个域大于64会生成16字节的位图
        int[] l2 = {1, 2, 4, 6, 7, 76, 87};
        System.out.println("生成128域位图：" + bitMapFormat(l2));

        String btm = "0020000100C00012";
        System.out.println("解析64域图：" + getBitMapNum(btm));

        String btm2 = "D6000000000000000010420200001521";
        System.out.println("解析128域图：" + getBitMapNum(btm2));
    }
}