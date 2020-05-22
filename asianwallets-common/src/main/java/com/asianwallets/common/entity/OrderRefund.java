package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;


@Table(name = "order_refund")
@Data
@ApiModel(value = "退款", description = "退款")
public class OrderRefund extends BaseEntity {

    @ApiModelProperty(value = "退款类型")// 1：全额退款 2：部分退款
    @Column(name = "refund_type")
    private Byte refundType;

    @ApiModelProperty(value = "退款方式")//1：系统退款 2：人工退款
    @Column(name = "refund_mode")
    private Byte refundMode;

    @ApiModelProperty(value = "退款状态")//1:退款中 2:退款成功 3:退款失败 4:系统创建失败
    @Column(name = "refund_status")
    private Byte refundStatus;

    @ApiModelProperty(value = "产品类型")//1-收款 2-付款
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易类型")//1-线上 2-线下
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "代理商编号")
    @Column(name = "agency_code")
    private String agencyCode;

    @ApiModelProperty(value = "代理商名称")
    @Column(name = "agency_name")
    private String agencyName;

    @ApiModelProperty(value = "集团商户编号")
    @Column(name = "group_merchant_code")
    private String groupMerchantCode;

    @ApiModelProperty(value = "集团商户名称")
    @Column(name = "group_merchant_name")
    private String groupMerchantName;

    @ApiModelProperty(value = "二级商户名称")
    @Column(name = "second_merchant_name")
    private String secondMerchantName;

    @ApiModelProperty(value = "二级商户编码")
    @Column(name = "second_merchant_code")
    private String secondMerchantCode;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "商户订单时间")
    @Column(name = "merchant_order_time")
    private Date merchantOrderTime;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户订单金额")
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "商户订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "汇率(订单币种对交易币种的汇率)")
    @Column(name = "order_for_trade_rate")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "汇率(交易币种对订单币种的汇率)")
    @Column(name = "trade_for_order_rate")
    private BigDecimal tradeForOrderRate;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "操作员ID")
    @Column(name = "operator_id")
    private String operatorId;

    @ApiModelProperty(value = "token")
    @Column(name = "token")
    private String token;

    @ApiModelProperty(value = "换汇汇率")
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇时间")
    @Column(name = "exchange_time")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")//1-换汇成功 2-换汇失败
    @Column(name = "exchange_status")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "通道退款币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "通道退款金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "交易流水号")
    @Column(name = "order_id")
    private String orderId;

    @ApiModelProperty(value = "上报通道的流水号")
    @Column(name = "report_number")
    private String reportNumber;

    @ApiModelProperty(value = "退款单通道流水号")
    @Column(name = "refund_channel_number")
    private String refundChannelNumber;

    @ApiModelProperty(value = "通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "支付方式")
    @Column(name = "pay_method")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "浮动率")
    @Column(name = "float_rate")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "附加值")
    @Column(name = "add_value")
    private BigDecimal addValue;

    @ApiModelProperty(value = "付款人姓名")
    @Column(name = "payer_name")
    private String payerName;

    @ApiModelProperty(value = "付款人账户")
    @Column(name = "payer_account")
    private String payerAccount;

    @ApiModelProperty(value = "付款人银行")
    @Column(name = "payer_bank")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    @Column(name = "payer_email")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    @Column(name = "payer_phone")
    private String payerPhone;

    @ApiModelProperty(value = "Swift Code")
    @Column(name = "swift_code")
    private String swiftCode;

    @ApiModelProperty(value = "签名")
    @Column(name = "sign")
    private String sign;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "通道手续费类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate;

    @ApiModelProperty(value = "通道网关手续费")
    @Column(name = "channel_gateway_fee")
    private BigDecimal channelGatewayFee;

    @ApiModelProperty(value = "通道网关手续费类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "channel_gateway_fee_type")
    private String channelGatewayFeeType;

    @ApiModelProperty(value = "通道网关是否收取")//1-收 2-不收
    @Column(name = "channel_gateway_charge")
    private Byte channelGatewayCharge;

    @ApiModelProperty(value = "通道网关收取状态")//1-成功时收取 2-失败时收取 3-全收
    @Column(name = "channel_gateway_status")
    private Byte channelGatewayStatus;

    @ApiModelProperty(value = "产品结算周期")
    @Column(name = "product_settle_cycle")
    private String productSettleCycle;

    @ApiModelProperty(value = "上报通道的交易金额")
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;

    @ApiModelProperty(value = "手续费承担方")//1:商家 2:用户
    @Column(name = "fee_payer")
    private Byte feePayer;

    @ApiModelProperty(value = "通道手续费")
    @Column(name = "channel_fee")
    private BigDecimal channelFee;

    @ApiModelProperty(value = "退款费率类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "refund_rate_type")
    private String refundRateType;

    @ApiModelProperty(value = "退款费率")
    @Column(name = "refund_rate")
    private BigDecimal refundRate;

    @ApiModelProperty(value = "退款手续费(订单币种对交易币种的手续费)")
    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "退款手续费(交易币种对订单币种的手续费)")
    @Column(name = "refund_fee_trade")
    private BigDecimal refundFeeTrade;

    @ApiModelProperty(value = "退还收单手续费金额（订单币种）")
    @Column(name = "refund_order_fee")
    private BigDecimal refundOrderFee;

    @ApiModelProperty(value = "退还收单手续费金额(交易币种)")
    @Column(name = "refund_order_fee_trade")
    private BigDecimal refundOrderFeeTrade;

    @ApiModelProperty(value = "退款费率的最小值")
    @Column(name = "refund_min_tate")
    private BigDecimal refundMinTate;

    @ApiModelProperty(value = "退款费率的最大值")
    @Column(name = "refund_max_tate")
    private BigDecimal refundMaxTate;

    @ApiModelProperty(value = "银行机构号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "备注1")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "退款金额 部分/全部")
    @Column(name = "remark2")
    private String remark2;

    @ApiModelProperty(value = "从接口判断RV 或者 RF")
    @Column(name = "remark3")
    private String remark3;

    @ApiModelProperty(value = "从业务判断RV 或者 RF")
    @Column(name = "remark4")
    private String remark4;

    @ApiModelProperty(value = "remark5")
    @Column(name = "remark5")
    private String remark5;

    @ApiModelProperty(value = "remark6")
    @Column(name = "remark6")
    private String remark6;

    @ApiModelProperty(value = "remark7")
    @Column(name = "remark7")
    private String remark7;

    @ApiModelProperty(value = "remark8")
    @Column(name = "remark8")
    private String remark8;

    @ApiModelProperty(value = "银行卡号")
    @Column(name = "user_bank_card_no")
    private String userBankCardNo;

    @ApiModelProperty(value = "CVV2")
    @Column(name = "cvv")
    private String cvv;

    @ApiModelProperty(value = "卡有效期")
    @Column(name = "valid")
    private String valid;

    @ApiModelProperty(value = "磁道信息")
    @Column(name = "track_data")
    private String trackData;
}
