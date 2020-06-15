package com.asianwallets.common.constant;

/**
 * 常量类
 */
public class TradeConstant {
    //响应信息
    public static final String HTTP_SUCCESS = "200"; //成功状态码200
    public static final String HTTP_FAIL = "500"; //错误状态码500
    public static final String HTTP_SUCCESS_MSG = "success"; //success
    public static final String HTTP_FAIL_MSG = "fail"; //fail

    //退款失败调账记录
    public static final String REFUND_FAIL_RECONCILIATION = "退款失败调账记录"; //退款失败调账记录

    //撤销时退款用
    public static final String CANCEL_ORDER_REFUND_FAIL = "撤销时退款失败调账记录"; //撤销时退款失败调账记录

    //--------------签名类型
    public static final String RSA = "1"; //RSA签名方式
    public static final String MD5 = "2"; //MD5签名方式

    //--------------支付页面类型
    public static final String PAGE_SUCCESS = "2000"; //成功页面
    public static final String PAGE_PROCESSING = "1000"; //等待页面

    //--------------交易类型
    public static final Byte GATHER_TYPE = 1; //收
    public static final Byte PAY_TYPE = 2; //付

    //------------订单交易方向
    public static final Byte TRADE_ONLINE = 1; //线上
    public static final Byte TRADE_UPLINE = 2; //线下

    //------------产品交易方向
    public static final Byte PRODUCT_ONLINE = 1; //线上
    public static final Byte PRODUCT_OFFLINE = 2; //线下
    public static final Byte PRODUCT_UPLINE_MOVE = 3; //线下移动端

    //------------费率类型------------
    public static final String FEE_TYPE_RATE = "dic_7_1"; //单笔费率
    public static final String FEE_TYPE_QUOTA = "dic_7_2"; //单笔定额

    //----------是否退还收单手续费
    public static final Byte REFUND_ORDER_FEE = 1; //退还
    public static final Byte REFUND_NO_ORDER_FEE = 2; //不退还
    public static final Byte REFUND_TODAY_ORDER_FEE = 3; //仅限当日退还

    //----------手续费承担方 1:商家 2:用户
    public static final Byte FEE_PAYER_IN = 1; //商家
    public static final Byte FEE_PAYER_OUT = 2; //用户

    //-----------计费状态
    public static final Byte CHARGE_STATUS_SUCCESS = 1; //计费成功
    public static final Byte CHARGE_STATUS_FALID = 2; //计费失败
    public static final Byte CHARGE_STATUS_OTHER = 3; //其他计费失败

    //---------------审核状态 audit status
    public static final Byte AUDIT_WAIT = 1; //待审核
    public static final Byte AUDIT_SUCCESS = 2; //审核通过
    public static final Byte AUDIT_FAIL = 3; //审核不通过

    //---------------集团商户审核状态 audit status
    public static final Byte GROUP_AUDIT_SUCCESS = 1; //审核通过
    public static final Byte GROUP_AUDIT_FAIL = 2; //审核不通过

    //---------------付款状态 payment status
    public static final Byte PAYMENT_START = 1; //待付款
    public static final Byte PAYMENT_WAIT = 2; //付款中
    public static final Byte PAYMENT_SUCCESS = 3; //付款成功
    public static final Byte PAYMENT_FAIL = 4; //付款失败

    //--------------- 分润状态 shareBenefit status
    public static final Byte SHARE_BENEFIT_WAIT = 1; //待分润
    public static final Byte SHARE_BENEFIT_SUCCESS = 2; //已分润

    //---------------订单状态 order status
    public static final Byte ORDER_WAIT_PAY = 1; //待支付
    public static final Byte ORDER_PAYING = 2; //支付中
    public static final Byte ORDER_PAY_SUCCESS = 3; //支付成功
    public static final Byte ORDER_PAY_FAILD = 4; //支付失败
    public static final Byte ORDER_DATE_PASS = 5; //已过期
    public static final Byte ORDER_REFUND = 6; //退款

    //---------------订单撤销状态
    public static final Byte ORDER_CANNELING = 1; //撤销中
    public static final Byte ORDER_CANNEL_SUCCESS = 2; //撤销成功
    public static final Byte ORDER_CANNEL_FALID = 3; //撤销失败
    public static final Byte ORDER_RESEVALING = 4; //冲正中
    public static final Byte ORDER_RESEVAL_SUCCESS = 5; //冲正成功
    public static final Byte ORDER_RESEVAL_FALID = 6; //冲正失败

    //---------------原订单退款状态 order status
    public static final Byte ORDER_REFUND_WAIT = 1; //退款中
    public static final Byte ORDER_REFUND_PART_SUCCESS = 2; //部分退款成功
    public static final Byte ORDER_REFUND_SUCCESS = 3; //退款成功
    public static final Byte ORDER_REFUND_FAIL = 4; //退款失败

    //---------------订单清算状态 --------
    public static final Integer ORDER_CLEAR_WAIT = 1; //待清算
    public static final Integer ORDER_CLEAR_SUCCESS = 2; //已清算

    //---------------订单结算状态 --------
    public static final Integer ORDER_SETTLE_WAIT = 1; //待结算
    public static final Integer ORDER_SETTLE_SUCCESS = 2; //已结算

    //--------------换汇状态swap_status
    public static final Byte SWAP_SUCCESS = 1; //换汇成功
    public static final Byte SWAP_FALID = 2; //换汇失败

    //--------------退款状态状态
    public static final Byte REFUND_WAIT = 1; //退款中
    public static final Byte REFUND_SUCCESS = 2; //退款成功
    public static final Byte REFUND_FALID = 3; //退款失败
    public static final Byte REFUND_SYS_FALID = 4; //系统创建退款单失败

    //---------------退款类型
    public static final Byte REFUND_TYPE_TOTAL = 1; //全额退款
    public static final Byte REFUND_TYPE_PART = 2; //部分退款

    //---------------通道网关是否收取
    public static final Byte CHANNEL_GATEWAY_CHARGE_YES = 1; //收
    public static final Byte CHANNEL_GATEWAY_CHARGE_NO = 2; //不收

    //---------------通道网关收取状态
    public static final Byte CHANNEL_GATEWAY_CHARGE_NOT_STATUS = 0; //不收取
    public static final Byte CHANNEL_GATEWAY_CHARGE_SUCCESS_STATUS = 1; //成功收
    public static final Byte CHANNEL_GATEWAY_CHARGE_FAILURE_STATUS = 2; //失败收
    public static final Byte CHANNEL_GATEWAY_CHARGE_ALL_STATUS = 3; //全收

    //---------------退款方式
    public static final Byte REFUND_MODE_AUTO = 1; //系统退款
    public static final Byte REFUND_MODE_PERSON = 2; //人工退款

    //---------------交易限额限次
    public static final String DAILY_TOTAL_AMOUNT_KEY = "DAILY_TOTAL_AMOUNT_KEY"; //日交易限额redis标记
    public static final String DAILY_TRADING_COUNT_KEY = "DAILY_TRADING_COUNT_KEY"; //日交易笔数redis标记

    //多语言
    public static final String ZH_HK = "zh-hk";//繁体
    public static final String EN_US = "en-us";//英文
    public static final String VN = "vn";//越南
    public static final String ZH_CN = "zh-cn";//中文

    //下单重复请求redis key标识
    public static final String REPEATED_REQUEST_KEY = "REPEATED_REQUEST_KEY";


    //---------------清结算类型
    public static final Integer CLEARING = 1; //清算
    public static final Integer SETTLE = 2; //结算

    //---------------清结算交易类型
    public static final String NT = "NT"; //收单
    public static final String CZ = "CZ"; //冲正
    public static final String RF = "RF"; //退款
    public static final String RV = "RV"; //撤销
    public static final String WD = "WD"; //提款
    public static final String AA = "AA"; //调账
    public static final String RA = "RA"; //撤销调账
    public static final String PM = "PM"; //付款
    public static final String PAYING = "PAYING"; //订单付款中

    //---------------线上订单的类型
    public static final Byte DIRECTCONNECTION = 1; //直连订单
    public static final Byte INDIRECTCONNECTION = 2; //间连订单

    //---------------资金类型
    public static final Integer NORMAL_FUND = 1; //正常资金
    public static final Integer FROZEN_FUND = 2; //冻结资金

    //亚洲钱包清结算返回成功的场合
    public final static String CLEARING_SUCCESS = "T000";
    //亚洲钱包清结算返回失败的场合
    public final static String CLEARING_FAIL = "T001";

    //---------------交易限额限次
    public static final String DAILY_TOTAL_AMOUNT = "DAILY_TOTAL_AMOUNT"; //日交易限额redis标记
    public static final String DAILY_TRADING_COUNT = "DAILY_TRADING_COUNT"; //日交易笔数redis标记

    //--------------调账状态
    public static final int RECONCILIATION_WAIT = 1; //待调账
    public static final int RECONCILIATION_SUCCESS = 2; //调账成功
    public static final int RECONCILIATION_FALID = 3; //调账失败
    public static final int FREEZE_WAIT = 4; //待冻结
    public static final int FREEZE_SUCCESS = 5; //冻结成功
    public static final int FREEZE_FALID = 6; //冻结失败
    public static final int UNFREEZE_WAIT = 7; //待解冻
    public static final int UNFREEZE_SUCCESS = 8; //解冻成功
    public static final int UNFREEZE_FALID = 9; //解冻失败

    //--------------资金变动类型
    public static final byte TRANSFER = 1;//调账
    public static final byte FUND_FREEZING = 2;//资金冻结
    public static final byte THAWING_FUNDS = 3;//资金解冻


    //--------------通道订单交易状态
    public static final Byte TRADE_WAIT = 1; //待交易
    public static final Byte TRADE_SUCCESS = 2; //交易成功
    public static final Byte TRADE_FALID = 3; //交易失败

    //------------通道标识
    public static final String AD3 = "AD3";
    public static final String MEGAPAY = "MEGAPAY";
    public static final String HELP2PAY = "HELP2PAY";
    public static final String NEXTPOS = "NEXTPOS";
    public static final String ENETS = "ENETS";
    public static final String NGANLUONG = "NGANLUONG";
    public static final String TH = "TH";
    public static final String UPI = "UPI";

    //--------------发货状态
    public static final Byte UNSHIPPED = 1; //未发货
    public static final Byte SHIPPED = 2; //已发货

    //--------------签收状态
    public static final Byte NO_RECEIVED = 1; //未签收
    public static final Byte RECEIVED = 2; //已签收

    //------------冻结类型
    public static final Integer FREEZING_FUND = 1;//资金冻结
    public static final Integer RESERVATION_FREEZE = 2;//预约冻结

    //------------冻结资金记录的状态
    public static final Integer HAVE_FROZEN = 1;//已冻结
    public static final Integer FROZEN_FALID = 3;//冻结失败

    //------------线下二维码解码类型
    public static final String NO_DECODE = "0";//不用解码
    public static final String BASE_64 = "1";//Base64解码

    //----------机构结算交易的key
    public static final String FLAY_KEY = "SETTLE_ORDER_TASK";

    //----------机构产品结算周期类型
    public static final String DELIVERED = "妥投结算";
    public static final String FUTURE_TIME = "2088-08-08 08:08:08";
    public static final String SHOULD_STIME = "2088-08-08";

    //----------构造表单
    public static final String START = "<html>" +
            "<head><title>asianwallets</title>" +
            "    <script language=\"JavaScript\">function onLoadHandler() {" +
            "        document.asianwallets.submit();" +
            "    }</script>" +
            "    <head>" +
            "<body onload=\"onLoadHandler()\"><form name=\"asianwallets\" action=\"";
    public static final String END = "\" method=\"POST\"></form></body>" +
            "</html>";

    //--------------收单类型
    public static final String SCAN_CODE = "SCAN"; //扫码
    public static final String ONLINE_BANKING = "NETBANK"; //网银
    public static final String SCAN_DECODE = "DECODE"; //enets线下扫码 需要Base64
    public static final String CHANNELS = "CHANNELS"; //通道网银系统
    public static final String ENETS_BANK = "ENETS_BANK"; //enets网银
    public static final String NEXTPOS_AD3 = "TAIQR"; //NEXTPOST线上扫码
    public static final String ALIPAY = "ALIPAY"; //ALIPAY线上扫码
    public static final String WECHAT = "WECHAT"; //WECHAT线上扫码
    public static final String CLOUD = "CLOUD"; //云闪付
    public static final String EGHL = "EGHL"; //EGHL
    public static final String DOKU = "DOKU"; //DOKU
    public static final String QFPAY = "QFPAY"; //QFPAY

    //为了保证分润插入发生异常或者产品信息不完整 或者 代理商产品算费失败 邮件只发一次
    public static final String MERCHANT_CHARGE_FAIL = "MERCHANT_CHARGE_FAIL";

    public static final String AGENCY_CHARGE_FAIL = "AGENCY_CHARGE_FAIL";

    public static final String FR_INSERT_EXCEPTION = "FR_INSERT_EXCEPTION";

    //预授权订单表的订单状态
    public static final Byte PRE_ORDER_SUCCESS=1;//预授权成功
    public static final Byte PRE_ORDER_FAIL=2;//预授权失败
    public static final Byte PRE_ORDER_RESVER_SUCCESS=3;//冲正成功
    public static final Byte PRE_ORDER_CANCEL_SUCCESS=4;//撤销成功
    public static final Byte PRE_ORDER_COMPLETE_SUCCESS=5;//预授权完成

}
