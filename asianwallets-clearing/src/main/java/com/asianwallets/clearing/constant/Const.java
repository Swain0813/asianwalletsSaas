package com.asianwallets.clearing.constant;
import com.asianwallets.clearing.utils.I18nUtil;

/**
 * 系统中的常量
 * @author Administrator
 *
 */
public class Const {

	public static String getTextValue(String key) {
	        return I18nUtil.getApplicationContext().getMessage(key, null, null);
	 }
	public interface Redis {

		//清结算key
		public static final String CLEARING_KEY = "CLEARING_KEY";

		//redis分布式key过期时间
		public static final int expireTime  = 60*1000;
	 }
     /**
      * 公共类型的code
      * @author admin
      *
      */
	public interface Code{

		/**
		 * 日期格式字符串常量 "yyyy-MM-dd"
		 */
		public static final String PATTERN_DATE = "yyyy-MM-dd";

		/**
		 * 日期+时间格式字符串常量 "yyyy-MM-dd HH:mm:ss"
		 */
		public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

		/**
		 * 空字符串 ""
		 */
		public static final String EMPTY_STRING = "";

		/**
		 * 数字0
		 */
		public static final int NUM_0 = 0x0;

		//以下为公共代码
		public static final String CODE_T000 = "T000";
		public static final String MSG_T000 = "success";

		public static final String CODE_T001 = "T001";
		public static final String MSG_T001 = "系统错误";

		public static final String CODE_T002 = "T002";
		public static final String MSG_T002 = "请求超时";

		public static final String CODE_T003 = "T003";
		public static final String MSG_T003 = "商户订单号重复";

		public static final String OK   = "OK";
		public static final String OK_MSG   = "操作成功";

		public static final String FAILED  = "FAILED";
		public static final String FAILED_MSG  = "操作失败";

		public static final String SELECT_FAILED  = "SELECT_FAILED";
		public static final String SELECT_FAILED_MSG  = "查询失败";

		public static final String CODE_T004 = "T004";
		public static final String MSG_T004 = "退款失败";

		public static final String CODE_NoData = "T005";
		public static final String MSG_NoData = "无数据";

		public static final String CODE_NoParameter = "T006";
		public static final String MSG_NoParameter= "无参数或者参数不全";

		public static final String CODE_ReqeustFail = "T007";
		public static final String MSG_ReqeustFail= "请求通信过程失败";

		public static final String CODE_ConnectFail = "T008";
		public static final String MSG_ConnectFail= "连接失败";

		public static final String CODE_NoChannelAvailable = "T009";
		public static final String MSG_NoChannelAvailable= "无可用通道";

		public static final String CODE_RequestSignError = "T010";
		public static final String MSG_RequestSignError = "请求签名错误";

		public static final String CODE_ResponseSignError = "T011";
		public static final String MSG_ResponseSignError = "返回验签错误";

		public static final String CODE_GetResponseFail = "T012";
		public static final String MSG_GetResponseFail= "返回信息处理失败";

		public static final String CODE_LackOfFunds = "T013";
		public static final String MSG_LackOfFunds= "账户资金不足";

		public static final String CODE_RequestClearingSettlementError = "T014";
		public static final String MSG_RequestClearingSettlementError= "请求清结算异常";

		public static final String CODE_UpdateFail = "T015";
		public static final String MSG_UpdateFail= "更新处理异常";

		public static final String CODE_ValidateFail = "T016";
		public static final String MSG_ValidateFail= "校验失败";

		public static final String CODE_InsertFail = "T017";
		public static final String MSG_InsertFail= "插入失败";

		public static final String CODE_DealError = "T018";
		public static final String MSG_DealError= "处理过程异常";

		public static final String CODE_VersionIllegal = "T019";
		public static final String MSG_VersionIllegal= "version字段不合法";

		public static final String CODE_InputCharsetIllegal = "T020";
		public static final String MSG_InputCharsetIllegal= "InputCharset字段不合法";

		public static final String CODE_LanguageIllegal = "T021";
		public static final String MSG_LanguageIllegal= "language字段不合法";

		public static final String CODE_MerchantIdIllegal = "T023";
		public static final String MSG_MerchantIdIllegal= "MerchantId字段不合法";

		public static final String CODE_MerOrderNoIllegal = "T024";
		public static final String MSG_MerOrderNoIllegal= "MerOrderNo字段不合法";

		public static final String CODE_CurrencyIllegal = "T025";
		public static final String MSG_CurrencyIllegal= "Currency字段不合法";

		public static final String CODE_TxnamountIllegal = "T026";
		public static final String MSG_TxnamountIllegal= "交易金额字段不合法";

		public static final String CODE_MvaccountIdIllegal = "T027";
		public static final String MSG_MvaccountIdIllegal= "MvaccountId字段不合法";

		public static final String CODE_StateIllegal = "T028";
		public static final String MSG_StateIllegal= "State字段不合法";

		public static final String CODE_SignMsgIllegal = "T030";
		public static final String MSG_SignMsgIllegal= "signMsg字段不合法";

		public static final String CODE_OrganNotExitMid = "T031";
		public static final String MSG_OrganNotExitMid= "机构商户关系不存在";

	}



	/**
	 * 清结算系统特有的code
	 * @author admin
	 *
	 */
	public interface CSCode{
		/*  1、待支付 消费者下单后的状态
        2、支付失败 消费者尝试支付，并且没有成功支付记录
        */
		public static final String CODE_CS0000 = "CS0000";
		public static final String MSG_CS0000 = "请求报文为空";

		public static final String CODE_CS0001 = "CS0001";
		public static final String MSG_CS0001 = "版本号不合法";

		public static final String CODE_CS0002 = "CS0002";
		public static final String MSG_CS0002 = "字符集不合法";

		public static final String CODE_CS0003 = "CS0003";
		public static final String MSG_CS0003 = "语言不合法";

		public static final String CODE_CS0004 = "CS0004";
		public static final String MSG_CS0004 = "提交参数不完整";

		public static final String CODE_CS0005 = "CS0005";
		public static final String MSG_CS0005 = "商户编号不合法";

		public static final String CODE_CS0006 = "CS0006";
		public static final String MSG_CS0006 = "资金类型不合法";

		public static final String CODE_CS0007 = "CS0007";
		public static final String MSG_CS0007 = "操作（查询）异常";

		public static final String CODE_CS0008 = "CS0008";
		public static final String MSG_CS0008 = "签名错误";

		public static final String CODE_CS0009 = "CS0009";
		public static final String MSG_CS0009 = "操作失败";

		public static final String CODE_CS00010 = "CS00010";
		public static final String MSG_CS00010 = "查询不到原始待结算记录";

		public static final String CODE_CS00011 = "CS00011";
		public static final String MSG_CS00011 = "商户信息不符合操作要求";

		public static final String CODE_CS00012 = "CS00012";
		public static final String MSG_CS00012 = "日期(时间)格式异常";

		public static final String CODE_CS00013 = "CS00013";
		public static final String MSG_CS00013 = "应结日期应该大于等于当前日期";

		public static final String CODE_CS00014 = "CS00014";
		public static final String MSG_CS00014 = "手续费币种，通道成本必须和结算币种一致";

		public static final String CODE_CS00015 = "CS00015";
		public static final String MSG_CS00015 = "交易金额和结算金额正负号必须一致";

		public static final String CODE_CS00023 = "CS00023";
		public static final String MSG_CS00023 = "结算状态异常";

		public static final String CODE_CS00024 = "CS00024";
		public static final String MSG_CS00024 = "没有查询到符合的数据";

		public static final String CODE_CS00025 = "CS00025";
		public static final String MSG_CS00025 = "查询接口查询异常";

		public static final String CODE_CS00026 = "CS00026";
		public static final String MSG_CS00026 = "接入机构号不合法";

		public static final String CODE_CS00027 = "CS00027";
		public static final String MSG_CS00027 = "数据查询异常";

		public static final String CODE_CS00028 = "CS00028";
		public static final String MSG_CS00028 = "业务类型不合法";

		public static final String CODE_CS00029 = "CS00029";
		public static final String MSG_CS00029 = "账户类型不合法";

		public static final String CODE_CS00030 = "CS00030";
		public static final String MSG_CS00030 = "交易类型不合法";

		public static final String CODE_CS00031 = "CS00031";
		public static final String MSG_CS00031 = "清算状态不合法";

		public static final String CODE_CS00032 = "CS00032";
		public static final String MSG_CS00032 = "资金类型不合法";

		public static final String CODE_CS00033 = "CS00033";
		public static final String MSG_CS00033 = "转出商户账号不合法";

		public static final String CODE_CS00034 = "CS00034";
		public static final String MSG_CS00034 = "转出商户账号不合法";

		public static final String CODE_CS00035 = "CS00035";
		public static final String MSG_CS00035 = "金额不合法";

		public static final String CODE_CS00036 = "CS00036";
		public static final String MSG_CS00036 = "币种不合法";

		public static final String CODE_CS00037 = "CS00037";
		public static final String MSG_CS00037 = "手续费不合法";

		public static final String CODE_CS00038 = "CS00038";
		public static final String MSG_CS00038 = "通道成本不合法";

		public static final String CODE_CS00039 = "CS00039";
		public static final String MSG_CS00039 = "商户所属机构不匹配";

		public static final String CODE_CS00040 = "CS00040";
		public static final String MSG_CS00040 = "清算资金不能加冻结";
	}


}
