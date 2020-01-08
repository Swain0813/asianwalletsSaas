package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "人工回调DTO", description = "人工回调DTO")
public class ArtificialDTO {

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "交易状态")
    private Byte tradeStatus;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "用户名")
    private String userName;
}
