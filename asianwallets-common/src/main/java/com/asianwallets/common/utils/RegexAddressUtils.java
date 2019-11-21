package com.asianwallets.common.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version v1.0.0
 * @classDesc: 功能描述: 正则表达式地址工具类
 * @createTime 2018/8/2 下午5:12
 * @copyright: 上海众哈网络技术有限公司
 */
public class RegexAddressUtils {


    /**
     * @methodDesc: 功能描述: 获取省市区县
     * @author Wu, Hua-Zheng
     * @createTime 2018/8/2 下午5:13
     * @version v1.0.0
     */
    public static Map<String, String> regexAddress(String address) {

        String regex = "((?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)|上海|北京|天津|重庆)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null, village = null;
        Map<String, String> resultMap = null;
        while (m.find()) {
            resultMap = new LinkedHashMap<String, String>();
            province = m.group("province");
            resultMap.put("province", province == null ? "" : province.trim());
            city = m.group("city");
            resultMap.put("city", city == null ? "" : city.trim());
            county = m.group("county");
            resultMap.put("county", county == null ? "" : county.trim());
            town = m.group("town");
            resultMap.put("town", town == null ? "" : town.trim());
            village = m.group("village");
            resultMap.put("village", village == null ? "" : village.trim());
        }
        return resultMap;
    }


    /**
     * @methodDesc: 功能描述: 地址中截取区县信息
     * @author Wu, Hua-Zheng
     * @createTime 2018/8/2 下午5:52
     * @version v1.0.0
     */
    /**
     * @methodDesc: 功能描述: 获取省市区县
     * @author Wu, Hua-Zheng
     * @createTime 2018/8/2 下午5:13
     * @version v1.0.0
     */
    public static String getCounty(String address) {


        String county = null;
        String regex = "(?<city>.*?市|上海|北京|天津|重庆|上海市|北京市|天津市|重庆市)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)";
        Matcher m = Pattern.compile(regex).matcher(address);
        while (m.find()) {

            county = m.group("county");

            county = county == null ? "" : county.trim();
            if (ValidatorToolUtils.isNotNullOrEmpty(county)) {
                if (county.indexOf("区") >= 0) {
                    county = county.substring(0, county.indexOf("区") + 1);
                }
            } else {
                county = RegexAddressUtils.getCounty1(address);
            }

        }
        if (ValidatorToolUtils.isNullOrEmpty(county)) {
            county = RegexAddressUtils.getCounty1(address);
        }

        if (ValidatorToolUtils.isNotNullOrEmpty(county)) {
            if (county.indexOf("市") >= 0) {
                county = county.substring(0, county.indexOf("市") + 1);
            }
        }

        if (ValidatorToolUtils.isNullOrEmpty(county)) {
            county = address;
            if (county.indexOf("市") >= 0) {
                county = county.substring(0, county.indexOf("市") + 1);
            }
        }

        if (ValidatorToolUtils.isNullOrEmpty(county) && ValidatorToolUtils.isNotNullOrEmpty(address)) {
            county = getCity(address);
            if (ValidatorToolUtils.isNullOrEmpty(county) && county.indexOf("市") >= 0) {
                county = county.substring(0, county.indexOf("市") + 1);
            }
        }

        return county;
    }


    public static String getCity(String address) {
        String regex = "((?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)|上海|北京|天津|重庆)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String city = null;
        while (m.find()) {
            city = m.group("city");
            city = city == null ? "" : city.trim();
        }
        return city;
    }


    public static String getCounty1(String address) {


        String county = null;
        String regex = "(?<county>[^县]+县|.+区|.+旗|.+海域|.+岛)";
        Matcher m = Pattern.compile(regex).matcher(address);
        while (m.find()) {

            county = m.group("county");
            county = county == null ? "" : county.trim();
            if (ValidatorToolUtils.isNotNullOrEmpty(county)) {
                if (county.indexOf("区") >= 0) {
                    county = county.substring(0, county.indexOf("区") + 1);
                }
            }

        }
        return county;
    }

//    public static void main(String[] args) {
//        System.out.println(RegexAddressUtils.getCounty("北京市顺义区仁和地区河南村白家街54号"));
//        System.out.println(RegexAddressUtils.getCounty("福清市龙田镇东营村龙顶3号"));
//        System.out.println(RegexAddressUtils.getCounty("平潭县华夏超市内顺兴楼一层"));
//        System.out.println(RegexAddressUtils.getCounty("闽清县梅城镇解放大街270号广宇超市内"));
//        System.out.println(RegexAddressUtils.getCounty("福州市长乐区潭头镇福星村二组29号"));
//        System.out.println(RegexAddressUtils.getCounty("福建省福州市长乐区金峰镇中心区规划8号金港城商业中心永辉超市A0721221"));
//        System.out.println(RegexAddressUtils.getCounty("福建省福州市福清市石竹街道棋山村福耀工业区1区商场东南面一层"));
//        System.out.println(RegexAddressUtils.getCounty("长乐市鹤上镇京林村三叉路"));
//    }

}

