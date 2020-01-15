package com.asianwallets.trade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "线上查询订单信息输出实体", description = "线上查询订单信息输出实体")
public class OnlineCheckOrdersVO {

    @ApiModelProperty(value = "系统订单流水号")
    private String referenceNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户订单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "订单状态 1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期")
    private Byte txnStatus;

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date txnTime;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;

    @ApiModelProperty(value = "退款状态 1-退款中 2-部分退款成功 3-退款成功 4-退款失败")
    private Byte refundStatus;

    @ApiModelProperty(value = "撤销状态 1-撤销中 2-撤销成功 3-撤销失败")
    private Byte reverseStatus;

}
