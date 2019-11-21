package com.asianwallets.common.utils;

import com.asianwallets.common.utils.validation.impl.DateFormatImpl;
import com.asianwallets.common.utils.validation.impl.DecimalFormatImpl;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public interface Constant {
    /*** 静态常量 */
    String ZERO = "0";
    String SHA1 = "SHA-1";
    String MD5 = "MD5";

    BigDecimal ZERO_DECIMAL = new BigDecimal(ZERO);
    BigDecimal ONE = new BigDecimal("1");

    long SERIAL_VERSION_UID = -3597809070303833860L;
    SecureRandom random = new SecureRandom();
    Lock LOCK = new ReentrantLock();
    int DEFAULT_PAGE_SIZE = 10;
    Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");
    Pattern INT_PATTERN = Pattern.compile("^\\d+$");
    Pattern FLOAT_PATTERN = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
    String SUCCEED = "succeed";
    String OK = "ok";
    String ERROR = "error";
    String FAILURE = "failure";
    String COMMAND = "command";
    char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    String GBK = "GBK";

    String DEFAULT_ENCODING = "UTF-8";

    String[] padding = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          "};

    ImmutableList<Character> CHAR_NUMBER_SEQUENCE = ImmutableList.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    ImmutableList<Character> CHARS_EQUENCE = ImmutableList.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W');

    ImmutableList<Character> NUMBER_SEQUENCE = ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    int YEAR = Calendar.getInstance().get(Calendar.YEAR);
    /**
     * 整数
     */
    DecimalFormatImpl INTEGET = new DecimalFormatImpl("#########");
    /**
     * 小数
     */
    DecimalFormatImpl FLOAT_0 = new DecimalFormatImpl("########0.0");
    DecimalFormatImpl FLOAT_00 = new DecimalFormatImpl("########0.00");
    DecimalFormatImpl FLOAT_000 = new DecimalFormatImpl("########0.000");
    DecimalFormatImpl FLOAT_0000 = new DecimalFormatImpl("########0.0000");
    DecimalFormatImpl FLOAT_00000 = new DecimalFormatImpl("########0.00000");
    DecimalFormatImpl FLOAT_000000 = new DecimalFormatImpl("########0.000000");
    DecimalFormatImpl FLOAT_0000000 = new DecimalFormatImpl("########0.0000000");
    DecimalFormatImpl FLOAT_00000000 = new DecimalFormatImpl("########0.00000000");
    DecimalFormatImpl FLOAT_000000000000 = new DecimalFormatImpl("################0.000000000000");

    DateFormatImpl yymmdd = new DateFormatImpl("yyMMdd");

    /**
     * 年周数
     */
    DateFormatImpl yyyyw = new DateFormatImpl("yyyyw");
    /**
     * 秒
     */
    DateFormatImpl second = new DateFormatImpl("ss");
    /**
     * 分钟
     */
    DateFormatImpl minute = new DateFormatImpl("mm");
    /**
     * 24小时
     */
    DateFormatImpl hour24 = new DateFormatImpl("HH");
    /**
     * 24小时
     */
    DateFormatImpl hhmm = new DateFormatImpl("HHmm");
    DateFormatImpl hh = new DateFormatImpl("HH");
    DateFormatImpl mm = new DateFormatImpl("mm");
    /**
     * 24小时
     */
    DateFormatImpl hhmmss = new DateFormatImpl("HHmmss");
    /**
     * 24小时
     */
    DateFormatImpl hh_mm_ss = new DateFormatImpl("HH:mm:ss");
    /**
     * 12小时
     */
    DateFormatImpl hour12 = new DateFormatImpl("hh");
    /**
     * 天
     */
    DateFormatImpl day = new DateFormatImpl("dd");
    /**
     * 月
     */
    DateFormatImpl month = new DateFormatImpl("MM");
    /**
     * 年
     */
    DateFormatImpl year = new DateFormatImpl("yyyy");
    /**
     * 年-月
     */
    DateFormatImpl yyyy_mm = new DateFormatImpl("yyyy-MM");
    /**
     * MM-dd
     */
    DateFormatImpl mm_dd = new DateFormatImpl("MM-dd");
    /**
     * 年-月-日
     */
    DateFormatImpl yyyy_mm_dd = new DateFormatImpl("yyyy-MM-dd");
    /**
     * 年月
     */
    DateFormatImpl yyyymm = new DateFormatImpl("yyyyMM");
    /**
     * 月中-周
     */
    DateFormatImpl MMWW = new DateFormatImpl("MMWW");
    /**
     * 年中第几周
     */
    DateFormatImpl yyyyww = new DateFormatImpl("yyyyww");
    /**
     * 年中第几周
     */
    DateFormatImpl WW = new DateFormatImpl("WW");
    /**
     * 年中第几周
     */
    DateFormatImpl ww = new DateFormatImpl("ww");
    /**
     * 年月日
     */
    DateFormatImpl yyyymmdd = new DateFormatImpl("yyyyMMdd");
    /**
     * 年月日时
     */
    DateFormatImpl yyyymmddhh = new DateFormatImpl("yyyyMMddHH");
    /**
     * 年月日时分
     */
    DateFormatImpl yyyymmddhhmm = new DateFormatImpl("yyyyMMddHHmm");
    /**
     * 年月日时分
     */
    DateFormatImpl yyyymmddhhmmss = new DateFormatImpl("yyyyMMddHHmmss");
    /**
     * 年-月-日 时:分:秒 毫秒
     */
    DateFormatImpl yyyymmddhhmmsssss = new DateFormatImpl("yyyyMMddHHmmssSSS");
    /**
     * 年月日 时分秒
     */
    DateFormatImpl yyyymmdd_hhmmss = new DateFormatImpl("yyyyMMdd HHmmss");
    /**
     * 年-月-日 时:分:秒
     */
    DateFormatImpl yyyy_mm_dd_hh_mm = new DateFormatImpl("yyyy-MM-dd HH:mm");
    /**
     * 年-月-日 时:分:秒
     */
    DateFormatImpl yyyy_mm_dd_hh_mm_ss = new DateFormatImpl("yyyy-MM-dd HH:mm:ss");
    /**
     * 年-月-日 时:分:秒 毫秒
     */
    DateFormatImpl yyyy_mm_dd_hh_mm_ss_sss = new DateFormatImpl("yyyy-MM-dd HH:mm:ss SSS");

}
