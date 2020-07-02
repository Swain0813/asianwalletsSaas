package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "线下码牌交易输入实体", description = "线下码牌交易输入实体")
public class OfflineCodeTradeDTO {

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "扫码标志")
    private String userAgent;

    @ApiModelProperty(value = "聚合码id")
    private String merchantCardCode;
}
