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
@Table(name = "order_refund")
@ApiModel(value = "退款订单", description = "退款订单")
public class OrderRefund extends BaseEntity {

    @ApiModelProperty(value = "退款类型 1：全额退款 2：部分退款")
    @Column(name = "refund_type")
    private Byte refundType;

    @ApiModelProperty(value = "退款方式 1：系统退款 2：人工退款")
    @Column(name = "refund_mode")
    private Byte refundMode;

    @ApiModelProperty(value = "退款状态 1：退款中 2：退款成功 3：退款失败")
    @Column(name = "refund_status")
    private Byte refundStatus;

    @ApiModelProperty(value = "交易类型:1-收 2-付")
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易方向:1-收 2-付")
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "二级商户名称")
    @Column(name = "second_institution_name")
    private String secondInstitutionName;

    @ApiModelProperty(value = "二级商户编码")
    @Column(name = "second_institution_code")
    private String secondInstitutionCode;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "商户所在地的时区记录")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "institution_order_time")
    private Date institutionOrderTime;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "institution_order_id")
    private String institutionOrderId;

    @ApiModelProperty(value = "机构订单金额")
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @ApiModelProperty(value = "机构订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "商户的请求商品名称")
    @Column(name = "commodity_name")
    private String commodityName;

    @ApiModelProperty(value = "产品编码")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "产品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "通道编码")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;


    @ApiModelProperty(value = "通道手续费")
    @Column(name = "channel_fee")
    private BigDecimal channelFee;

    @ApiModelProperty(value = "退款手续费")
    @Column(name = "refund_fee")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "通道手续费类型")
    @Column(name = "channel_fee_type")
    private String channelFeeType;

    @ApiModelProperty(value = "通道费率")
    @Column(name = "channel_rate")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "通道网关费率")
    @Column(name = "channel_gateway_rate")
    private BigDecimal channelGatewayRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "通道网关手续费")
    @Column(name = "channel_gateway_fee")
    private BigDecimal channelGatewayFee = BigDecimal.ZERO;
    ;

    @ApiModelProperty(value = "通道网关手续费类型")
    @Column(name = "channel_gateway_fee_type")
    private String channelGatewayFeeType;//dic_7_1-单笔费率,dic_7_2-单笔定额

    @ApiModelProperty(value = "通道网关是否收取")
    @Column(name = "channel_gateway_charge")
    private Byte channelGatewayCharge;//1-收 2-不收

    @ApiModelProperty(value = "通道网关收取状态")
    @Column(name = "channel_gateway_status")
    private Byte channelGatewayStatus;// 1-成功时收取 2-失败时收取 3-全收

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "exchange_time")
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

    @ApiModelProperty(value = "原订单交易流水号")
    @Column(name = "order_id")
    private String orderId;

    @ApiModelProperty(value = "原订单通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "退款单通道流水号")
    @Column(name = "refund_channel_number")
    private String refundChannelNumber;


    @ApiModelProperty(value = "付款方式")
    @Column(name = "pay_method")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "浮动率")
    @Column(name = "float_rate")
    private BigDecimal floatRate = BigDecimal.ZERO;

    @ApiModelProperty(value = "附加值")
    @Column(name = "add_value")
    private BigDecimal addValue = BigDecimal.ZERO;

    @ApiModelProperty(value = "货品描述")
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

    /**
     * 2019-11-08 从付款人电话改成Swift Code
     */
    @ApiModelProperty(value = "Swift Code")
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

    @ApiModelProperty(value = "产品结算周期")
    @Column(name = "product_settle_cycle")
    private String productSettleCycle;

    @ApiModelProperty(value = "退款费率类型")//dic_7_1-单笔费率,dic_7_2-单笔定额
    @Column(name = "refund_rate_type")
    private String refundRateType;

    @ApiModelProperty(value = "退款费率")
    @Column(name = "refund_rate")
    private BigDecimal refundRate;

}