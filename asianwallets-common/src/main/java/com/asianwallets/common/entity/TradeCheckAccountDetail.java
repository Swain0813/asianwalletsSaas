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
@Table(name = "trade_check_account_detail")
@ApiModel(value = "交易对账单详情", description = "交易对账单详情")
public class TradeCheckAccountDetail extends BaseEntity {

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

    @ApiModelProperty(value = "订单id")
    @Column(name = "order_id")
    private String orderId;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "订单创建时间")
    @Column(name = "order_create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty(value = "交易类型")
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易状态")
    @Column(name = "trade_status")
    private Byte tradeStatus;

    @ApiModelProperty(value = "撤销状态")
    @Column(name = "cancel_status")
    private Byte cancelStatus;

    @ApiModelProperty(value = "退款状态")
    @Column(name = "refund_status")
    private Byte refundStatus;

    @ApiModelProperty(value = "交易方向")
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "费率类型")
    @Column(name = "rate_type")
    private String rateType;

    @ApiModelProperty(value = "费率")
    @Column(name = "rate")
    private BigDecimal rate;

    @ApiModelProperty(value = "支付方式")
    @Column(name = "pay_type")
    private String payType;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private BigDecimal fee;

    @ApiModelProperty(value = "最小值")
    @Column(name = "min_tate")
    private BigDecimal minTate = BigDecimal.ZERO;

    @ApiModelProperty(value = "最大值")
    @Column(name = "max_tate")
    private BigDecimal maxTate = BigDecimal.ZERO;

    @ApiModelProperty(value = "支付完成时间")
    @Column(name = "channel_callback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;
}