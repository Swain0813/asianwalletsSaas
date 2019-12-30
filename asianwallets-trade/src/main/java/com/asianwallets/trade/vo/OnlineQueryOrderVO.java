package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "线上查询订单状态输出实体", description = "线上查询订单状态输出实体")
public class OnlineQueryOrderVO {

    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @ApiModelProperty(value = "交易状态")//1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期)
    private Byte txnstatus;
}
