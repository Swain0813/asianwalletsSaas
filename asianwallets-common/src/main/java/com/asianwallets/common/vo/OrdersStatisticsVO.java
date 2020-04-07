package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "交易统计VO", description = "交易统计VO")
public class OrdersStatisticsVO {

    @ApiModelProperty(value = "币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "交易笔数")
    private Integer totalCount;

}
