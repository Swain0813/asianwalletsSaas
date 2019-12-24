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

    //撤销更新订单失败
    public final static String CX_GX_FAIL_DL = "CX_GX_FAIL_DL";
    public static final String E_CX_GX_FAIL_DL = "E_CX_GX_FAIL_DL";
    public static final String CX_GX_FAIL_DL_KEY = "CX_GX_FAIL_DL_KEY";
    public static final String CX_GX_FAIL_DL_EXCHANGE = "CX_GX_FAIL_DL_EXCHANGE";

    //撤销上报上游失败
    public final static String CX_SB_FAIL_DL = "CX_SB_FAIL_DL";
}
