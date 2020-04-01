package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "QfPay服务器回调实体", description = "QfPay服务器回调实体")
public class QfPayCallbackDTO {

    @ApiModelProperty(value = "订单号")
    private String orderNum;

    @ApiModelProperty(value = "订单状态 0000为交易成功")
    private String status;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "签名")
    private String sign;
}
