package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易对账详细表输出实体的英文输入实体", description = "交易对账详细表输出实体的英文输入实体")
public class TradeCheckAccountDetailEnVO {

    @ApiModelProperty("AW Order Id")
    private String orderId;

    @ApiModelProperty("Institution Id")
    private String institutionCode;

    @ApiModelProperty("Order Creation Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty("Device Code")
    private String deviceCode;

    @ApiModelProperty("Institution Order Id")
    private String institutionOrderId;

    @ApiModelProperty("Payment Method")
    private String payType;

    @ApiModelProperty("Currency")
    private String orderCurrency;

    @ApiModelProperty("Order Amount")
    private BigDecimal amount;

    @ApiModelProperty("Trade Type")
    private String tradeTypeName;

    @ApiModelProperty("Trade Status")
    private String tradeStatusName;

    @ApiModelProperty("Trade Completion Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payFinishTime;

    @ApiModelProperty("Fee Type")
    private String rateType;

    @ApiModelProperty("Rate")
    private BigDecimal rate;

    @ApiModelProperty("Fee")
    private BigDecimal fee;
}
