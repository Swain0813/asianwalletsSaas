package com.asianwallets.trade.utils;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.entity.Holidays;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.HolidaysMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class SettleDateUtil {

    @Autowired
    private HolidaysMapper holidaysMapper;

    private static SettleDateUtil settleDateUtil;

    @PostConstruct
    public void init() {
        settleDateUtil = this;
        settleDateUtil.holidaysMapper = this.holidaysMapper;
    }

    /**
     * 获取中国T+N开头的结算周期
     *
     * @param settleCycle 结算周期
     * @return date
     */
    public static String getSettleDate(String settleCycle) {
        int offset = Integer.parseInt(settleCycle.substring(2));//偏移量
        //T+0实时清结算
        //if (offset == 0) {
        //获取结算日
        Date calcDate = get(new Date(), offset, "中国");
        //计算出来的结算日的字符串格式
        String calcDateString = DateToolUtils.getReqDate(calcDate);
        //今天的字符串格式
        String today = DateToolUtils.getReqDate(new Date());
        //如果计算出来的结算日是今天,那么实时结算
        if (calcDateString.equals(today)) {
            return DateToolUtils.formatDate(calcDate);
        } else {
            ////如果计算出来的结算日不是今天,那么从当日零点开始
            return DateToolUtils.formatDate(DateToolUtils.getDayStart(calcDate));
        }
    }


    /**
     * 判断时间是否为周末
     *
     * @param date date
     * @return 布尔值
     */
    public static boolean judgeWeekend(Date date) {
        int flag = DateUtil.dayOfWeek(date);
        return flag == 1 || flag == 7;
    }

    /**
     * 判断日期是否为节假日
     *
     * @param date date
     * @return 布尔值
     */
    public static boolean judgeHolidays(Date date, String country) {
        Holidays holidays = settleDateUtil.holidaysMapper.selectByDateAndCountry(DateToolUtils.getReqDate(date), country);
        return holidays != null;
    }

    /**
     * 得到偏移量之后的日期
     *
     * @param date   date
     * @param offset 偏移量
     * @return date
     */
    public static Date getLastDate(Date date, Integer offset) {
        return DateUtil.offsetDay(date, offset).toJdkDate();
    }

    /**
     * 返回偏移后不是周末或者节假日的日期
     *
     * @param date    日期
     * @param offset  偏移量
     * @param country 国家
     * @return date
     */
    private static Date get(Date date, Integer offset, String country) {
        //获取偏移后的日期
        Date dateTime = getLastDate(date, offset);
        //判断是否是周末或者节假日
        if (judgeWeekend(dateTime) || judgeHolidays(dateTime, country)) {
            offset++;
            return get(date, offset, country);
        } else {
            return dateTime;
        }
    }
}



