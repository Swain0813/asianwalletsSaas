package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易对账详细表输出实体", description = "交易对账详细表输出实体")
public class TradeCheckAccountDetailVO {

    @ApiModelProperty("订单流水号")
    private String orderId;

    @ApiModelProperty("商户编号")
    private String merchantId;

    @ApiModelProperty("订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty("设备编号")
    private String deviceCode;

    @ApiModelProperty("商户订单号")
    private String merchantOrderId;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("订单币种")
    private String orderCurrency;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("交易类型")
    private Byte tradeType;

    @ApiModelProperty("交易类型名称")
    private String tradeTypeName;

    @ApiModelProperty("交易状态")
    private Byte tradeStatus;

    @ApiModelProperty("退款状态")
    private Byte refundStatus;

    @ApiModelProperty("交易状态名称")
    private String tradeStatusName;

    @ApiModelProperty("支付完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty("费率类型")
    private String rateType;

    @ApiModelProperty("费率")
    private BigDecimal rate;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "最小值")
    private BigDecimal minTate = BigDecimal.ZERO;

    @ApiModelProperty(value = "最大值")
    private BigDecimal maxTate = BigDecimal.ZERO;
}