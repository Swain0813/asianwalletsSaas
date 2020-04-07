package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "产品交易统计VO", description = "产品交易统计VO")
public class OrdersProStatisticsVO {

    @ApiModelProperty(value = "币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "交易笔数")
    private Integer totalCount;

    @ApiModelProperty(value = "创建时间")
    public String createTime;

    @ApiModelProperty(value = "产品名称")
    public String productName;
}
