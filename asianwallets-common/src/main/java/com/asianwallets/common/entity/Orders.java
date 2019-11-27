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

    @ApiModelProperty(value = "交易类型")
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易方向")
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "二级机构名称")
    @Column(name = "second_institution_name")
    private String secondInstitutionName;

    @ApiModelProperty(value = "二级机构编码")
    @Column(name = "second_institution_code")
    private String secondInstitutionCode;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "机构所在地的时区记录")
    @Column(name = "institution_order_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date institutionOrderTime;

    @ApiModelProperty(value = "机构订单号")
    @Column(name = "institution_order_id")
    private String institutionOrderId;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "汇率(订单币种对交易币种的汇率)")
    @Column(name = "commodity_name")
    private String commodityName;

    @ApiModelProperty(value = "商户的回调地址")
    @Column(name = "return_url")
    private String returnUrl;

    @ApiModelProperty(value = "浏览器跳转地址")
    @Column(name = "jump_url")
    private String jumpUrl;

    @ApiModelProperty(value = "上游手续费")
    @Column(name = "up_channel_fee")
    private String upChannelFee;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "device_code")
    private String deviceCode;

    @ApiModelProperty(value = "设备操作员")
    @Column(name = "device_operator")
    private String deviceOperator;

    @ApiModelProperty(value = "换汇汇率")
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "换汇时间")
    @Column(name = "exchange_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")
    @Column(name = "exchange_status")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "交易状态")//交易状态:1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期
    @Column(name = "trade_status")
    private Byte tradeStatus;

    @ApiModelProperty(value = "退款状态")//退款状态：1-退款中 2-部分退款成功 3-退款成功 4-退款失败
    @Column(name = "refund_status")
    private Byte refundStatus;

    @ApiModelProperty(value = "撤销状态")//撤销状态：1-撤销中 2-撤销成功 3-撤销失败
    @Column(name = "cancel_status")
    private Byte cancelStatus;

    @ApiModelProperty(value = "通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "清算状态")
    @Column(name = "clear_status")
    private Byte clearStatus;

    @ApiModelProperty(value = "结算状态")
    @Column(name = "settle_status")
    private Byte settleStatus;

    @ApiModelProperty(value = "手费率类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "rate_type")
    private String rateType;

    @ApiModelProperty(value = "费率")
    @Column(name = "rate")
    private BigDecimal rate = BigDecimal.ZERO;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private BigDecimal fee = BigDecimal.ZERO;
    ;

    @ApiModelProperty(value = "手续费付款方")
    @Column(name = "fee_payer")
    private Byte feePayer;

    @ApiModelProperty(value = "计费状态")
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

    @ApiModelProperty(value = "浮动率")
    @Column(name = "float_rate")
    private BigDecimal floatRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "附加值")
    @Column(name = "add_value")
    private BigDecimal addValue = BigDecimal.ZERO;

    @ApiModelProperty(value = "汇率(交易币种转换订单币种的汇率)")
    @Column(name = "goods_description")
    private String goodsDescription;

    @ApiModelProperty(value = "付款人名称")
    @Column(name = "drawee_name")
    private String draweeName;

    @ApiModelProperty(value = "付款人账户")
    @Column(name = "drawee_account")
    private String draweeAccount;

    @ApiModelProperty(value = "付款人银行")
    @Column(name = "drawee_bank")
    private String draweeBank;

    @ApiModelProperty(value = "付款人邮箱")
    @Column(name = "drawee_email")
    private String draweeEmail;

    @ApiModelProperty(value = "付款人电话")
    @Column(name = "drawee_phone")
    private String draweePhone;

    @ApiModelProperty(value = "签名")
    @Column(name = "sign")
    private String sign;

    @ApiModelProperty(value = "备注1")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "备注2")
    @Column(name = "remark2")
    private String remark2;

    @ApiModelProperty(value = "备注3")
    @Column(name = "remark3")
    private String remark3;

    @ApiModelProperty(value = "备注4")
    @Column(name = "remark4")
    private String remark4;

    @ApiModelProperty(value = "通道手续费")
    @Column(name = "channel_fee")
    private BigDecimal channelFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道手续费类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道网关手续费")
    @Column(name = "channel_gateway_fee")
    private BigDecimal channelGatewayFee = BigDecimal.ZERO;

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

    @ApiModelProperty(value = "银行机构号：微信填写wechat支付宝填写alipay")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "代理机构编号")
    @Column(name = "agency_code")
    private String agencyCode;
}