package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
@ApiModel(value = "订单", description = "订单")
public class Orders extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "二级商户名称")
    @Column(name = "second_merchant_name")
    private String secondMerchantName;

    @ApiModelProperty(value = "二级商户编号")
    @Column(name = "second_merchant_code")
    private String secondMerchantCode;

    @ApiModelProperty(value = "代理编号")
    @Column(name = "agent_code")
    private String agentCode;

    @ApiModelProperty(value = "代理名称")
    @Column(name = "agent_name")
    private String agentName;

    @ApiModelProperty(value = "集团商户编号")
    @Column(name = "group_merchant_code")
    private String groupMerchantCode;

    @ApiModelProperty(value = "集团商户名称")
    @Column(name = "group_merchant_name")
    private String groupMerchantName;

    @ApiModelProperty(value = "产品类型")//1-收款 2-付款
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易类型")//1-线上 2-线下
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户订单时间")
    @Column(name = "merchant_order_time")
    private Date merchantOrderTime;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户类型 3-普通商户 4-代理商户 5-集团商户")
    @Column(name = "merchant_type")
    private String merchantType;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "order_amount")
    private BigDecimal orderAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    @Column(name = "operator_id")
    private String operatorId;

    @ApiModelProperty(value = "订单币种转交易币种汇率")
    @Column(name = "order_for_trade_rate")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "交易币种转订单币种汇率")
    @Column(name = "trade_for_order_rate")
    private BigDecimal tradeForOrderRate;

    @ApiModelProperty(value = "换汇汇率")
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "换汇时间")
    @Column(name = "exchange_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")//1-换汇成功 2-换汇失败
    @Column(name = "exchange_status")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    @Column(name = "product_description")
    private String productDescription;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "交易状态")//1-待付款 2-付款中 3-付款成功 4-付款失败 5-已过期
    @Column(name = "trade_status")
    private Byte tradeStatus;

    @ApiModelProperty(value = "撤销状态")//1-撤销中 2-撤销成功 3-撤销失败
    @Column(name = "cancel_status")
    private Byte cancelStatus;

    @ApiModelProperty(value = "退款状态")//1-退款中  2-部分退款成功 3-退款成功 4-退款失败
    @Column(name = "refund_status")
    private Byte refundStatus;

    @ApiModelProperty(value = "订单间连直连类型")//1-直连 2-间连
    @Column(name = "connect_method")
    private Byte connectMethod;

    @ApiModelProperty(value = "结算状态")//1-待结算 2-结算成功 3-结算失败
    @Column(name = "settle_status")
    private Byte settleStatus;

    @ApiModelProperty(value = "通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "计费状态")//1-成功,2-失败
    @Column(name = "charge_status")
    private Byte chargeStatus;

    @ApiModelProperty(value = "计费时间")
    @Column(name = "charge_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date chargeTime;

    @ApiModelProperty(value = "付款方式")
    @Column(name = "pay_method")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "通道金额")
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;

    @ApiModelProperty(value = "上报通道的流水号")
    @Column(name = "report_number")
    private String reportNumber;

    @ApiModelProperty(value = "上报通道时间")
    @Column(name = "report_channel_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "通道回调时间")
    @Column(name = "channel_callback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "通道手续费")
    @Column(name = "up_channel_fee")
    private BigDecimal upChannelFee;

    @ApiModelProperty(value = "浮动率")
    @Column(name = "float_rate")
    private BigDecimal floatRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "附加值")
    @Column(name = "add_value")
    private BigDecimal addValue = BigDecimal.ZERO;

    @ApiModelProperty(value = "付款人名称")
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

    @ApiModelProperty(value = "付款人地址")
    @Column(name = "payer_address")
    private String payerAddress;

    @ApiModelProperty(value = "发货单号")
    @Column(name = "invoice_no")
    private String invoiceNo;

    @ApiModelProperty(value = "服务商名称")
    @Column(name = "provider_name")
    private String providerName;

    @ApiModelProperty(value = "运输商简码")
    @Column(name = "courier_code")
    private String courierCode;

    @ApiModelProperty(value = "发货时间")
    @Column(name = "delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    @ApiModelProperty(value = "发货状态")//1-未发货 2-已发货
    @Column(name = "delivery_status")
    private Byte deliveryStatus;

    @ApiModelProperty(value = "签收状态")//1-未签收 2-已签收 默认是未签收
    @Column(name = "received_status")
    private Byte receivedStatus;

    @ApiModelProperty(value = "签收时间")
    @Column(name = "received_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receivedTime;

    @ApiModelProperty(value = "产品结算周期")
    @Column(name = "product_settle_cycle")
    private String productSettleCycle;

    @ApiModelProperty(value = "银行机构号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "浏览器回调地址")
    @Column(name = "browser_url")
    private String browserUrl;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "server_url")
    private String serverUrl;

    @ApiModelProperty(value = "手续费承担方")//1:商家 2:用户
    @Column(name = "fee_payer")
    private Byte feePayer;

    @ApiModelProperty(value = "手续费费率类型")
    @Column(name = "rate_type")
    private String rateType;

    @ApiModelProperty(value = "手续费费率")
    @Column(name = "rate")
    private BigDecimal rate = BigDecimal.ZERO;

    @ApiModelProperty(value = "手续费(订单币种对交易币种的手续费)")
    @Column(name = "fee")
    private BigDecimal fee = BigDecimal.ZERO;

    @ApiModelProperty(value = "手续费(交易币种对订单币种的手续费)")
    @Column(name = "fee_trade")
    private BigDecimal feeTrade = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道手续费类型")
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道手续费")
    @Column(name = "channel_fee")
    private BigDecimal channelFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道网关是否收取")//1-收 2-不收
    @Column(name = "channel_gateway_charge")
    private Byte channelGatewayCharge;

    @ApiModelProperty(value = "通道网关收取状态")//1-成功时收取 2-失败时收取 3-全收
    @Column(name = "channel_gateway_status")
    private Byte channelGatewayStatus;

    @ApiModelProperty(value = "通道网关手续费类型")
    @Column(name = "channel_gateway_fee_type")
    private String channelGatewayFeeType;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道网关手续费")
    @Column(name = "channel_gateway_fee")
    private BigDecimal channelGatewayFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "签名")
    @Column(name = "sign")
    private String sign;

    @ApiModelProperty(value = "remark1")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    @Column(name = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    @Column(name = "remark3")
    private String remark3;

    @ApiModelProperty(value = "remark4")
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
}