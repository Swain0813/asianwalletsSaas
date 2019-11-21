package com.asianwallets.common.constant;

/**
 * @description: mq对列
 * @author: YangXu
 * @create: 2019-03-15 14:54
 **/
public class AD3MQConstant {

    /* ===========================================      调账队列       =============================================== */
    public static final String TC_MQ_RECONCILIATION_DL = "TC_MQ_RECONCILIATION_DL";//调账队列
    public static final String TC_MQ_FINANCE_TKBUDAN_DL = "TC_MQ_FINANCE_TKBUDAN_DL";//退款补单队列
    public static final String TC_MQ_FINANCE_SDBUDAN_DL = "TC_MQ_FINANCE_SDBUDAN_DL";//收单补单队列

    /* ===========================================      下单队列       =============================================== */
    public static final String MQ_PLACE_ORDER_FUND_CHANGE_FAIL = "MQ_PLACE_ORDER_FUND_CHANGE_FAIL";//支付成功上报清结算失败队列

    /* ===========================================      退款队列       =============================================== */
    public static final String MQ_TK_XX_QQSB_DL = "MQ_TK_XX_QQSB_DL";//退款请求失败 （线下）
    public static final String MQ_TK_XS_QQSB_DL = "MQ_TK_XS_QQSB_DL";//退款请求失败 （线上）
    public static final String MQ_CX_XX_QQSB_DL = "MQ_CX_XX_QQSB_DL";//撤销请求失败对列 （线下）
    public static final String MQ_CX_XS_QQSB_DL = "MQ_CX_XS_QQSB_DL";//撤销请求失败对列 （线上）
    public static final String MQ_QJS_TZSB_DL = "MQ_QJS_TZSB_DL";//上报清结算调账失败
    public static final String MQ_TK_SBQJSSB_DL = "MQ_TK_SBQJSSB_DL";//退款上报清结算失败
    public static final String MQ_CX_SBQJSSB_DL = "MQ_CX_SBQJSSB_DL";//撤销上报清结算失败

    public static final String MQ_TK_APLIPAY_QQSB_DL = "MQ_TK_APLIPAY_QQSB_DL";//alipay退款请求失败
    public static final String MQ_TK_WECHAT_QQSB_DL = "MQ_TK_WECHAT_QQSB_DL";//wechat退款请求失败
    public static final String MQ_TK_NEXTPOS_QQSB_DL = "MQ_TK_NEXTPOS_QQSB_DL";//NextPos退款请求失败

    public static final String MQ_CX_TDTKSB_DL = "MQ_CX_TDTKSB_DL";//撤销通道退款失败
    public static final String E_MQ_CX_TDTKSB_DL = "E_MQ_CX_TDTKSB_DL";//撤销通道退款失败死信队列
    public static final String MQ_CX_TDTKSB_DL_KEY = "MQ_CX_TDTKSB_DL_KEY";//撤销通道退款失败
    public static final String MQ_CX_TDTKSB_DL_EXCHANGE = "MQ_CX_TDTKSB_DL_EXCHANGE";//撤销通道退款失败死信路由

    /* ===========================================      撤销队列       =============================================== */
    public static final String MQ_AD3_ORDER_QUERY = "MQ_AD3_ORDER_QUERY";//撤销付款中订单查询AD3订单信息队列
    public static final String E_MQ_AD3_ORDER_QUERY = "E_MQ_AD3_ORDER_QUERY";//撤销付款中订单查询AD3订单信息死信队列
    public static final String MQ_AD3_ORDER_QUERY_KEY = "MQ_AD3_ORDER_QUERY_KEY";//撤销付款中订单查询AD3订单信息key
    public static final String MQ_AD3_ORDER_QUERY_EXCHANGE = "MQ_AD3_ORDER_QUERY_EXCHANGE";//撤销付款中订单查询AD3订单信息死信路由




    public static final String MQ_AD3_REFUND = "MQ_AD3_REFUND";//AD3退款队列
    public static final String TC_MQ_CANCEL_ORDER = "TC_MQ_CANCEL_ORDER";//撤销时更新订单表失败队列
    public static final String MQ_CANCEL_ORDER_FUND_CHANGE_RV_FAIL = "MQ_CANCEL_ORDER_FUND_CHANGE_RV_FAIL";//撤销时调用清结算资金变动RV时发生失败时的队列
    public static final String MQ_CANCEL_ORDER_REQUEST_FAIL = "MQ_CANCEL_ORDER_REQUEST_FAIL";//撤销时撤销请求失败队列
    public static final String MQ_CANCEL_ORDER_FUND_CHANGE_RF_FAIL = "MQ_CANCEL_ORDER_FUND_CHANGE_RF_FAIL";//撤销时退款调用清结算资金变动RF发生失败时队列
    public static final String MQ_CANCEL_ORDER_FUND_CHANGE_FAIL = "MQ_CANCEL_ORDER_FUND_CHANGE_FAIL";//撤销时退款调用清结算资金变动失败队列
    public static final String MQ_CANCEL_ORDER_CHANNEL_ACCEPT_FAIL = "MQ_CANCEL_ORDER_CHANNEL_ACCEPT_FAIL";//撤销时退款请求上游通道失败队列
    public static final String MQ_CANCEL_ORDER_CHANNEL_REFUND_FAIL = "MQ_CANCEL_ORDER_CHANNEL_REFUND_FAIL";//撤销时撤销通道退款失败

    /* ===========================================      回调队列       =============================================== */
    public static final String MQ_AW_CALLBACK_URL_FAIL = "MQ_AW_CALLBACK_URL_FAIL";//回调商户失败队列
    public static final String E_MQ_AW_CALLBACK_URL_FAIL = "E_MQ_AW_CALLBACK_URL_FAIL";//回调商户失败队列死信队列
    public static final String MQ_AW_CALLBACK_URL_FAIL_KEY = "MQ_AW_CALLBACK_URL_FAIL_KEY";//回调商户失败队列key
    public static final String MQ_AW_CALLBACK_URL_FAIL_EXCHANGE = "MQ_AW_CALLBACK_URL_FAIL_EXCHANGE";//回调商户失败队列
    /* ===========================================      汇款回调队列       =============================================== */
    public static final String MQ_PAYMENT_CALLBACK_URL_FAIL = "MQ_PAYMENT_CALLBACK_URL_FAIL";// 汇款回调商户失败队列
    public static final String E_MQ_PAYMENT_CALLBACK_URL_FAIL = "E_MQ_PAYMENT_CALLBACK_URL_FAIL";// 汇款回调商户失败队列死信队列
    public static final String MQ_PAYMENT_CALLBACK_URL_FAIL_KEY = "MQ_PAYMENT_CALLBACK_URL_FAIL_KEY";// 汇款回调商户失败队列key
    public static final String MQ_PAYMENT_CALLBACK_URL_FAIL_EXCHANGE = "MQ_PAYMENT_CALLBACK_URL_FAIL_EXCHANGE";// 汇款回调商户失败队列


    /* ===========================================      NganLuong查询队列1       =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL= "MQ_NGANLUONG_CHECK_ORDER_DL";// NganLuong查询订单状态队列
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL = "E_MQ_NGANLUONG_CHECK_ORDER_DL";//NganLuong查询订单状态队列死信队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY ="MQ_NGANLUONG_CHECK_ORDER_DL_KEY";//NganLuong查询订单状态队列
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE = "MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE";//NganLuong查询订单状态队列

    /* ===========================================      NganLuong查询队列2       =============================================== */
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL2= "MQ_NGANLUONG_CHECK_ORDER_DL2";// NganLuong查询订单状态队列
    public static final String E_MQ_NGANLUONG_CHECK_ORDER_DL2 = "E_MQ_NGANLUONG_CHECK_ORDER_DL2";//NganLuong查询订单状态队列死信队列2
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_KEY2 ="MQ_NGANLUONG_CHECK_ORDER_DL_KEY2";//NganLuong查询订单状态队列2
    public static final String MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2 = "MQ_NGANLUONG_CHECK_ORDER_DL_EXCHANGE2";//NganLuong查询订单状态队列2

    /* ===========================================      MegaPay-THB查询队列1       =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER= "MQ_MEGAPAY_THB_CHECK_ORDER";//MegaPay-THB查询队列1
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER = "E_MQ_MEGAPAY_THB_CHECK_ORDER";//MegaPay-THB查询死信队列1
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY ="MQ_MEGAPAY_THB_CHECK_ORDER_KEY";//MegaPay-THB查询死信队列1路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE = "MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE";//MegaPay-THB查询死信队列1交换机


    /* ===========================================      MegaPay-THB查询队列2       =============================================== */
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER2= "MQ_MEGAPAY_THB_CHECK_ORDER2";//MegaPay-THB查询队列2
    public static final String E_MQ_MEGAPAY_THB_CHECK_ORDER2 = "E_MQ_MEGAPAY_THB_CHECK_ORDER2";//MegaPay-THB查询死信队列2
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_KEY2 ="MQ_MEGAPAY_THB_CHECK_ORDER_KEY2";//MegaPay-THB查询死信队列2路由
    public static final String MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2 = "MQ_MEGAPAY_THB_CHECK_ORDER_EXCHANGE2";//MegaPay-THB查询死信队列2交换机

    /* ===========================================      冻结 解冻 失败队列       =============================================== */
    public static final String FREEZE_MQ_FAIL = "FREEZE_MQ_FAIL";//冻结
    public static final String UNFREEZE_MQ_FAIL = "UNFREEZE_MQ_FAIL";//解冻

    /* ===========================================      机构提款队列       =============================================== */
    public static final String TC_MQ_WD_DL = "TC_MQ_WD_DL";//机构自动提款队列
    public static final String TC_MQ_ZD_DL = "TC_MQ_ZD_DL";//机构手动提款队列

    /* ===========================================      分润队列       =============================================== */
    public static final String MQ_FR_DL = "MQ_FR_DL";//分润处理队列
}
