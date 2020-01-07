package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "机构日交易汇总表VO", description = "机构日交易汇总表VO")
public class InsDailyTradeVO {

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "总笔数")
    private Integer totalCount;

    @ApiModelProperty(value = "总订单金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "总手续费")
    public BigDecimal totalFee;
}