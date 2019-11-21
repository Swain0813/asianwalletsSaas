package com.asianwallets.common.utils.validation.impl;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatImpl extends SimpleDateFormat {

    private static final long serialVersionUID = 1L;

    public DateFormatImpl() {
        super();
    }

    public DateFormatImpl(String pattern) {
        super(pattern);
    }

    public DateFormatImpl(String pattern, Locale locale) {
        super(pattern, locale);
    }

    public DateFormatImpl(String pattern, DateFormatSymbols formatSymbols) {
        super(pattern, formatSymbols);
    }

    @Override
    public String toString() {
        return super.toPattern();
    }

    // 系统当前时间
    public synchronized final String formatCurrent() {
        return super.format(new Date());
    }

    // 系统当前时间
    public synchronized int formatIntCurrent() {
        return Integer.parseInt(replace(super.format(new Date())));
    }

    /**
     * 系统当前时间
     *
     * @return Long 系统当前时间
     */
    public synchronized Long formatLongCurrent() {
        return Long.parseLong(replace(super.format(new Date())));
    }

    /**
     * @param date 时间
     * @return long 当前时间
     */
    public synchronized long formatLong(Date date) {
        return Long.parseLong(replace(super.format(date)));
    }

    public synchronized String format(Long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        return super.format(instance.getTime());
    }

    public synchronized long formatLong(Long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        return Long.parseLong(replace(super.format(instance.getTime())));
    }

    public synchronized int formatInt(Long millis) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(millis);
        return Integer.parseInt(replace(super.format(instance.getTime())));
    }

    /**
     * @param date 日期
     * @return int 返回指定日期的整型格式
     */

    public synchronized int formatInt(Date date) {

        return Integer.parseInt(replace(super.format(date)));

    }

    /**
     * 系统当前时间
     *
     * @return Date 系统当前时间
     */
    public synchronized Date parseCurrent() {
        return parse(formatCurrent());
    }

    @Override
    public synchronized Date parse(String date) {
        Date source = null;
        try {
            source = super.parse(date);
        } catch (ParseException e) {
        }
        return source;
    }

    public synchronized Date parse(Integer date) {
        Date source = null;
        try {
            source = super.parse(date.toString());
        } catch (ParseException e) {
        }
        return source;
    }

    public synchronized Date parse(Long date) {
        Date source = null;
        try {
            source = super.parse(date.toString());
        } catch (ParseException e) {

        }
        return source;
    }

    public synchronized String replace(String date) {
        return date.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
    }
}
