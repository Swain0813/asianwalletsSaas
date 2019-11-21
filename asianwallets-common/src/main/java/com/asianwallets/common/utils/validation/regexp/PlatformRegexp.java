package com.asianwallets.common.utils.validation.regexp;

import org.apache.commons.lang3.StringUtils;

/**
 * @version v1.0.0
 * @classDesc: 类描述: 平台正则表达式定义
 * @createTime 2018年3月14日 下午8:37:53
 * @copyright: 上海众哈网络技术有限公司
 */
public class PlatformRegexp {

    public final static String NOT_BLANK_MSG = "不能为空";

    public final static String MUST_NUMERIC_MSG = "只能为数字";

    public final static String MAX_VALUE_MSG = "不能大于最大的允许值";

    public final static String MIN_VALUE_MSG = "不能少于最小的允许值";

    public final static String DATE_VALUE_MSG = "请输入正确的日期";

    public final static String WORD_CHOICE_ERROR_MSG = "请选择正确的选项";

    public final static String NORMALIZE_WORD = "\t|\r|\n";

    public final static String NORMALIZE_SPACE = "\\s*|\t|\r|\n";

    public final static String EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public final static String EMAIL_MSG = "请输入正确的邮箱地址。";

    /**
     * 规则说明： 1、可以是1开头的11位数字（手机号） 2、可以是“区号-电话号-分机号”或者是“(区号)电话号-分机号”格式
     * 3、区号是0开头的3～4位数字，可以没有区号 4、电话号是5～8位数字，不能以0开头 5、分机号是1～8位数字，可以没有分机号
     * <p>
     * 合法数据示例： 13812341234 010-12345678 (0432)1234567-1234
     */
    public final static String PHONE = "^1\\d{10}$|^(0\\d{2,3}-?|\\(0\\d{2,3}\\))?[1-9]\\d{4,7}(-\\d{1,8})?$";

    public final static String PHONE_MSG = "请输入正确的电话号码。";

    public final static String PASSWORD = "^\\w{6,18}$";

    public final static String PASSWORD_MSG = "密码长度在6~18之间，只能包含字符、数字和下划线。";

    /**
     * 1开头的11位数字（手机号） 合法数据示例： 13812341234
     */
    public final static String MOBILE = "^1[3|4|5|7|8][0-9]\\d{8}$";

    public final static String MOBILE_MSG = "请输入正确的手机号码。";

    public final static String SAFE_HTML = "(<(script[//s//S]*?))|(<(frameset [//s//S]*?))|(<(frame[//s//S]*?))|(<(form[//s//S]*?))|(<(input[//s//S]*?))";

    public final static String STRICT_HTML = SAFE_HTML + "|(<(a\\shref[//s//S]*?))";

    /**
     * @param regex
     * @param value
     * @methodDesc: 方法描述: 检验给定的值是否符合指定的正则表达式要求
     * @author Wu, Hua-Zheng
     * @createTime 2018年1月19日 下午8:38:55
     * @version v1.0.0
     */
    public static boolean validate(String regex, String value) {

        if (StringUtils.isBlank(value)) {
            return false;
        }
        return value.matches(regex);
    }

    /**
     * @param mobile
     * @return
     * @methodDesc: 功能描述:  手机号码验证
     * @author Wu, Hua-Zheng
     * @createTime 2018年7月8日 下午2:12:02
     * @version v1.0.0
     */
    public static boolean validateMobile(String mobile) {

        if (StringUtils.isBlank(mobile)) {
            return false;
        }

        return PlatformRegexp.validate(PlatformRegexp.MOBILE, mobile) || PlatformRegexp.validate(PlatformRegexp.PHONE, mobile);
    }

}
