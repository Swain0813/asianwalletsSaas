package com.asianwallets.common.constant;

/**
 * @description: mq对列
 * @author: YangXu
 * @create: 2019-03-15 14:54
 **/
public class AD3MQConstant {

    /*******************************************************  分润队列  ******************************************************************/
    public final static String SAAS_FR_DL = "SAAS_FR_DL";
    /********************************************************  退款接口相关队列 **********************************************************************/
    //RF or RV请求失败
    public final static String RV_RF_FAIL_DL = "RV_RF_FAIL_DL";
    //银行卡RF or RV请求失败
    public final static String BANK_RV_RF_FAIL_DL = "BANK_RV_RF_FAIL_DL";
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

    /* ===========================================      报备失败队列       =============================================== */
    public static final String MQ_REPORT_FAIL = "MQ_REPORT_FAIL";
    public static final String E_MQ_REPORT_FAIL = "E_MQ_REPORT_FAIL";
    public static final String MQ_REPORT_FAIL_KEY = "MQ_REPORT_FAIL_KEY";
    public static final String MQ_REPORT_FAIL_EXCHANGE = "MQ_REPORT_FAIL_EXCHANGE";

    //撤销更新订单失败
    public final static String CX_GX_FAIL_DL = "CX_GX_FAIL_DL";
    public static final String E_CX_GX_FAIL_DL = "E_CX_GX_FAIL_DL";
    public static final String CX_GX_FAIL_DL_KEY = "CX_GX_FAIL_DL_KEY";
    public static final String CX_GX_FAIL_DL_EXCHANGE = "CX_GX_FAIL_DL_EXCHANGE";

    //撤销上报上游失败
    public final static String CX_SB_FAIL_DL = "CX_SB_FAIL_DL";
    /* ===========================================      MegaPay-THB查询队列1       =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER = "MQ_MEGAPAY_THB_CHECK_ORDER";//MegaPay-THB查询队列1
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER = "E_MQ_MEGAPAY_THB_CHECK_ORDER";//MegaPay-THB查询死信队列1
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY = "MQ_MEGAPAY_THB_CHECK_ORDER_KEY";//MegaPay-THB查询死信队列1路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE = "MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE";//MegaPay-THB查询死信队列1交换机


    /* ===========================================      MegaPay-THB查询队列2       =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER2 = "MQ_MEGAPAY_THB_CHECK_ORDER2";//MegaPay-THB查询队列2
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER2 = "E_MQ_MEGAPAY_THB_CHECK_ORDER2";//MegaPay-THB查询死信队列2
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY2 = "MQ_MEGAPAY_THB_CHECK_ORDER_KEY2";//MegaPay-THB查询死信队列2路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2 = "MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2";//MegaPay-THB查询死信队列2交换机

    /* ===========================================      NganLuong查询队列1       =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL = "MQ_NGANLUONG_CHECK_ORDER_DL";// NganLuong查询订单状态队列
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL = "E_MQ_NGANLUONG_CHECK_ORDER_DL";//NganLuong查询订单状态队列死信队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY = "MQ_NGANLUONG_CHECK_ORDER_DL_KEY";//NganLuong查询订单状态队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE = "MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE";//NganLuong查询订单状态队列

    /* ===========================================      NganLuong查询队列2       =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL2 = "MQ_NGANLUONG_CHECK_ORDER_DL2";// NganLuong查询订单状态队列
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL2 = "E_MQ_NGANLUONG_CHECK_ORDER_DL2";//NganLuong查询订单状态队列死信队列2
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY2 = "MQ_NGANLUONG_CHECK_ORDER_DL_KEY2";//NganLuong查询订单状态队列2
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2 = "MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2";//NganLuong查询订单状态队列2

    /* ===========================================      Qfpay Refunding 查询队列      =============================================== */
    public static final String MQ_QFPAY_REFUND_SEARCH= "MQ_QFPAY_REFUND_SEARCH";//Qfpay查询队列2
    public static final String E_MQ_QFPAY_REFUND_SEARCH = "E_MQ_QFPAY_REFUND_SEARCH";//Qfpay查询死信队列2
    public static final String MQ_QFPAY_REFUND_SEARCH_KEY ="MQ_QFPAY_REFUND_SEARCH_KEY";//Qfpay查询死信队列2路由
    public static final String MQ_QFPAY_REFUND_SEARCH_EXCHANGE = "MQ_QFPAY_REFUND_SEARCH_EXCHANGE";//Qfpay查询死信队列2交换机
    /* ===========================================      Qfpay Canneling 查询队列      =============================================== */
    public static final String MQ_QFPAY_CANNEL_SEARCH = "MQ_QFPAY_CANNEL_SEARCH";//Qfpay查询队列2
    public static final String E_MQ_QFPAY_CANNEL_SEARCH = "E_MQ_QFPAY_CANNEL_SEARCH";//Qfpay查询死信队列2
    public static final String MQ_QFPAY_CANNEL_SEARCH_KEY = "MQ_QFPAY_CANNEL_SEARCH_KEY";//Qfpay查询死信队列2路由
    public static final String MQ_QFPAY_CANNEL_SEARCH_EXCHANGE = "MQ_QFPAY_CANNEL_SEARCH_EXCHANGE";//Qfpay查询死信队列2交换机
    /* ===========================================      QfPay-CSB查询队列      =============================================== */
    public static final String MQ_QFPAY_CSB_CHECK_ORDER = "MQ_QFPAY_CSB_CHECK_ORDER";//QFPAY-CSB查询队列
    public static final String E_MQ_QFPAY_CSB_CHECK_ORDER = "E_MQ_QFPAY_CSB_CHECK_ORDER";//QFPAY-CSB查询死信队列
    public static final String MQ_QFPAY_CSB_CHECK_ORDER_KEY = "MQ_QFPAY_CSB_CHECK_ORDER_KEY";//QFPAY-CSB查询死信队列路由
    public static final String MQ_QFPAY_CSB_CHECK_ORDER_EXCHANGE = "MQ_QFPAY_CSB_CHECK_ORDER_EXCHANGE";//QFPAY-CSB查询死信队列交换机

    /* ===========================================      通华查询队列      =============================================== */
    public static final String MQ_TH_CHECK_ORDER = "MQ_TH_CHECK_ORDER";//TH查询队列
    public static final String E_MQ_TH_CHECK_ORDER = "E_MQ_TH_CHECK_ORDER";//TH查询死信队列
    public static final String MQ_TH_CHECK_ORDER_KEY = "MQ_TH_CHECK_ORDER_KEY";//TH查询死信队列路由
    public static final String MQ_TH_CHECK_ORDER_EXCHANGE = "MQ_TH_CHECK_ORDER_EXCHANGE";//TH查询死信队列交换机
}
