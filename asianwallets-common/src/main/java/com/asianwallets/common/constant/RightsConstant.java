package com.asianwallets.common.constant;

/**
 * 权益
 */
public class RightsConstant {

    //优惠类型 1-满减 2-折扣 3-套餐 4-定额
    public static final Byte FULL_DISCOUNT = 1;
    public static final Byte DISCOUNT = 2;
    public static final Byte PACKAGE = 3;
    public static final Byte QUOTA = 4;


    //-------------- 票券状态
    public static final Byte TICKETS_WAIT = 1; //待领取
    public static final Byte TICKETS_NOT_USE = 2; //未使用
    public static final Byte TICKETS_USE = 3; //已使用
    public static final Byte TICKETS_OVERDUE = 4; //已过期
    public static final Byte TICKETS_REFUND = 5; //已退款

    //-------------- 核销状态
    public static final Byte HX_WAIT = 1; //核销中
    public static final Byte HX_SUCCESS = 2; //核销成功
    public static final Byte HX_FAIL = 3; //核销失败

    //-------------- 发布平台
    public static final Byte MOBILE_SYS = 1; //短信平台
    public static final Byte EMAIL_SYS= 2; //邮件平台

}
