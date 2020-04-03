package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "商户交易对账单英文输出实体", description = "商户交易对账单英文输出实体")
public class ExportTradeCheckAccountDetailEnVO {

    @ApiModelProperty("Order Create Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty(value = "Merchant Id")
    private String merchantId;

    @ApiModelProperty(value = "Merchant Name")
    private String merchantName;

    @ApiModelProperty(value = "Order Id")
    private String orderId;

    @ApiModelProperty("Merchant Order Id")
    private String merchantOrderId;

    @ApiModelProperty("Order Amount")
    private BigDecimal orderAmount;

    @ApiModelProperty("Trade Currency")
    private String orderCurrency;

    @ApiModelProperty("Trade Type")
    private String tradeTypeName;

    @ApiModelProperty("Trade Status")
    private String tradeStatusName;

    @ApiModelProperty("Trade direction")
    private Byte tradeDirection;

    @ApiModelProperty("Rate Type")
    private String rateType;

    @ApiModelProperty("Rate")
    private BigDecimal rate;

    @ApiModelProperty(value = "Guarantee Fee")
    private BigDecimal minTate = BigDecimal.ZERO;

    @ApiModelProperty(value = "Capping Fee")
    private BigDecimal maxTate = BigDecimal.ZERO;

    @ApiModelProperty("Fee")
    private BigDecimal fee;

    @ApiModelProperty("Pay Finish Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;
}
