package com.asianwallets.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "交易上报通道输入DTO", description = "交易上报通道输入DTO")
public class ChannelsRequestDTO {

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "支付url")
    private String payUrl;

    @ApiModelProperty(value = "通道单个查询url")
    private String channelSingleSelectUrl;

    @ApiModelProperty(value = "撤销url")
    private String voidUrl;

    @ApiModelProperty(value = "退款url")
    private String refundUrl;

    @ApiModelProperty(value = "extend1")
    private String extend1;

    @ApiModelProperty(value = "extend2")
    private String extend2;

    @ApiModelProperty(value = "extend3")
    private String extend3;

    @ApiModelProperty(value = "extend4")
    private String extend4;

    @ApiModelProperty(value = "extend5")
    private String extend5;

    @ApiModelProperty(value = "extend6")
    private String extend6;
}
