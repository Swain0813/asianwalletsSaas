package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "交易对账详细表输出实体", description = "交易对账详细表输出实体")
public class TradeAccountDetailVO {

    @ApiModelProperty("币种")
    private String orderCurrency;

    @ApiModelProperty("交易详情List")
    private List<TradeCheckAccountDetailVO> tradeCheckAccountDetailVOS;


}