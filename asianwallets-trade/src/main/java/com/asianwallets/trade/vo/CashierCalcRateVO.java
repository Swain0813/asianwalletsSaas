package com.asianwallets.trade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "汇率计算输出实体", description = "汇率计算输出实体")
public class CashierCalcRateVO {

    @ApiModelProperty(value = "换汇汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "订单币种转交易币种 原始汇率")
    private BigDecimal originalRate;

    @ApiModelProperty(value = "交易币种转订单币种 汇率")
    private BigDecimal reverseRate;

    @ApiModelProperty(value = "交易金额")
    private String tradeAmount;

    @ApiModelProperty(value = "换汇时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")
    private Byte exchangeStatus;


}
