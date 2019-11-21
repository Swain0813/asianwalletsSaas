package com.asianwallets.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 签名工具
 */
@Slf4j
public class SignTools {

    /**
     * 密文字符串的拼装处理
     *
     * @param m
     * @return
     */
    public static String getSignStr(Map<String, String> m) {
        String signstr = null;
        if (m != null) {
            /**
             * 排序map
             */
            Map<String, String> map = new TreeMap<>(
                    new Comparator<String>() {
                        public int compare(String obj1, String obj2) {
                            //
                            return obj1.compareTo(obj2);
                        }
                    });

            /**
             * 将请求map中的参数进行排序
             */
            Set<String> ks = m.keySet();
            if (ks != null) {
                Iterator it = ks.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (key != null && !key.equals("")) {
                        String value = m.get(key);
                        if (value != null && !value.equals("") && !value.equals("null")) {
                            map.put(key, value);
                        }
                    }
                }

            }

            /**
             * 取出value进行拼装签名前的字符
             */
            if (map != null && map.keySet() != null) {
                StringBuffer sb = new StringBuffer();
                Set<String> keySet = map.keySet();
                Iterator<String> iter = keySet.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    //System.out.println(key + ":" + map.get(key));
                    //这里要去掉空串和null
                    String val = map.get(key);
                    if (val != null && !val.equals("") && !val.equals("null")) {
                        sb.append(val.trim());
                    }
                }
                signstr = sb.toString();
                log.info("签名前的明文: " + signstr);
            }


        }
        return signstr;

    }


    /**
     * 密文字符串的拼装处理含有&和=
     *
     * @param m
     * @return
     */
    public static String getSignWithSymbol(Map<String, String> m) {
        String signstr = null;
        if (m != null) {
            /**
             * 排序map
             */
            Map<String, String> map = new TreeMap<String, String>(
                    new Comparator<String>() {
                        public int compare(String obj1, String obj2) {
                            //
                            return obj1.compareTo(obj2);
                        }
                    });

            /**
             * 将请求map中的参数进行排序
             */
            Set<String> ks = m.keySet();
            if (ks != null) {
                Iterator it = ks.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (key != null && !key.equals("")) {
                        String value = m.get(key);
                        //if(value!=null&&!value.equals("")&&!value.equals("null")){
                        map.put(key, value);
                        //}
                        if (StringUtils.equals("null", value) || null == value) {
                            map.put(key, "");
                        }
                    }
                }

            }

            /**
             * 取出valu进行拼装签名前的字符�?
             */
            if (map != null && map.keySet() != null) {
                StringBuffer sb = new StringBuffer();
                Set<String> keySet = map.keySet();
                Iterator<String> iter = keySet.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    log.info(key + ":" + map.get(key));
                    //这里要去掉空串和null
                    String val = map.get(key);
                    //if(val!=null&&!val.equals("")&&!val.equals("null")){
                    sb.append(key).append("=");

                    if (StringUtils.equals("null", val) || null == val) {
                        sb.append("").append("&");
                    }
                    sb.append(val.trim()).append("&");
                    //}
                }
                signstr = sb.toString();
                if (StringUtils.isNotBlank(signstr)) {
                    signstr = signstr.substring(0, signstr.length() - 1);
                }
                log.info("签名前的明文:" + signstr);
            }


        }
        return signstr;

    }

    /**
     * 注意，密文字符串的拼装处理, 值为null或"null"是替换成"", 参与排序和拼接
     *
     * @param m
     * @return
     */
    public static String getWXSignStr(Map<String, String> m) {
        String signstr = null;
        if (m != null) {
            /**
             * 排序map
             */
            Map<String, String> map = new TreeMap<String, String>(
                    new Comparator<String>() {
                        public int compare(String obj1, String obj2) {
                            //
                            return obj1.compareTo(obj2);
                        }
                    });

            /**
             * 将请求map中的参数进行排序
             */
            Set<String> ks = m.keySet();
            if (ks != null) {
                Iterator it = ks.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    if (key != null && !key.equals("")) {
                        String value = m.get(key);
                        if (value != null && !value.equals("") && !value.equals("null")) {
                            map.put(key, value);
                        }
//						if(StringUtils.equals("null", value)||null==value){
//							map.put(key, "");
//						}
                    }
                }

            }

            /**
             * 取出valu进行拼装签名前的字符�?
             */
            if (map != null && map.keySet() != null) {
                StringBuffer sb = new StringBuffer();
                Set<String> keySet = map.keySet();
                Iterator<String> iter = keySet.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    //System.out.println(key + ":" + map.get(key));
                    //这里要去掉空串和null
                    String val = map.get(key);
                    //if(val!=null&&!val.equals("")&&!val.equals("null")){
                    sb.append(key).append("=");

                    if (StringUtils.equals("null", val) || null == val) {
                        sb.append("").append("&");
                    }
                    sb.append(val.trim()).append("&");
                    //}
                }
                signstr = sb.toString();
                if (StringUtils.isNotBlank(signstr)) {
                    signstr = signstr.substring(0, signstr.length() - 1);
                }
                //log.info("签名前的明文:{}", signstr);
            }


        }
        return signstr;

    }

    /**
     * 微信MD5签名方式
     *
     * @param signTemp:排序后的参数字符串
     * @param key：key为商户平台设置的密钥key
     * @return：返回签名字符串
     */
    public static String getWXSign_MD5(String signTemp, String key) {
        String sign = null;
        if (signTemp != null && !signTemp.equals("") && key != null && !key.equals("")) {
            String stringSignTemp = signTemp + "&key=" + key;
            log.info("签名前的明文:{}", stringSignTemp);
            sign = MD5.MD5Encode(stringSignTemp).toUpperCase(); //注：MD5签名方式
            log.info("签名后的密文:{}", sign);
        }
        return sign;
    }

    /**
     * 微信HMAC-SHA256签名方式
     *
     * @param signTemp：排序后的参数字符串
     * @param key：key为商户平台设置的密钥key
     * @return ：返回签名字符串
     */
    public static String getWXSign_HMACSHA256(String signTemp, String key) {
        String sign = null;
        if (signTemp != null && !signTemp.equals("") && key != null && !key.equals("")) {
            String stringSignTemp = signTemp + "&key=" + key;
            System.out.println("签名前的明文:" + stringSignTemp);
            sign = MD5.sha256_HMAC(stringSignTemp, key).toUpperCase();//注：HMAC-SHA256签名方式
            System.out.println("签名后的密文:" + sign);
        }
        return sign;
    }

    public static void main(String[] args) {
        Map<String, String> map = new TreeMap<>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        return obj2.compareTo(obj1);
                    }
                });

        //Map<String, String> map = new TreeMap<>();
        map.put("c","3");
        map.put("a","1");
        map.put("b","2");
        System.out.println(map);
    }

}
