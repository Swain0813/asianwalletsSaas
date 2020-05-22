package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "支付宝线上下单浏览器回调实体", description = "支付宝线上下单浏览器回调实体")
public class AplipayBrowserCallbackDTO {
    @ApiModelProperty(value = "签名方式")
    private String sign_type;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;

    @ApiModelProperty(value = "支付宝交易号")
    private String trade_no;

    @ApiModelProperty(value = "交易状态")
    private String trade_status;
}
