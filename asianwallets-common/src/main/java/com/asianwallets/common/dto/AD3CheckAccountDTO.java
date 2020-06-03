package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
@ApiModel(value = "对账单实体", description = "对账单实体")
public class AD3CheckAccountDTO {

    @ApiModelProperty(value = "交易类型")
    private String type;

    @ApiModelProperty(value = "系统订单号")
    private String orderId;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "通道手续费")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "交易状态")
    private Byte status;

    @ApiModelProperty(value = "交易完成时间")
    private String tradeTime;

    public AD3CheckAccountDTO() {

    }
}
