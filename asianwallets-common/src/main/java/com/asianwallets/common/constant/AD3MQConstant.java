package com.asianwallets.common.constant;

/**
 * @description: mq对列
 * @author: YangXu
 * @create: 2019-03-15 14:54
 **/
public class AD3MQConstant {

    /********************************************************  退款接口相关队列 **********************************************************************/
    //RF or RV请求失败
    public final static String RV_RF_FAIL_DL = "RV_RF_FAIL_DL";
    //调账失败队列
    public final static String RA_AA_FAIL_DL = "RA_AA_FAIL_DL";
    //退款上报失败队列
    public final static String TK_SB_FAIL_DL = "TK_SB_FAIL_DL";

    //支付成功上报清结算失败队列
    public static final String MQ_PLACE_ORDER_FUND_CHANGE_FAIL = "MQ_PLACE_ORDER_FUND_CHANGE_FAIL";

    /* ===========================================      回调队列       =============================================== */
    public static final String MQ_AW_CALLBACK_URL_FAIL = "MQ_AW_CALLBACK_URL_FAIL";//回调商户失败队列
    public static final String E_MQ_AW_CALLBACK_URL_FAIL = "E_MQ_AW_CALLBACK_URL_FAIL";//回调商户失败队列死信队列
    public static final String MQ_AW_CALLBACK_URL_FAIL_KEY = "MQ_AW_CALLBACK_URL_FAIL_KEY";//回调商户失败队列key
    public static final String MQ_AW_CALLBACK_URL_FAIL_EXCHANGE = "MQ_AW_CALLBACK_URL_FAIL_EXCHANGE";//回调商户失败队列
}
