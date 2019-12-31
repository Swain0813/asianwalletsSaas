package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/6/05 15:08
 * @Description: megaPay服务器回调实体
 */
@Data
@ApiModel(value = "megaPay服务器回调实体", description = "megaPay服务器回调实体")
public class MegaPayServerCallbackDTO {

    @ApiModelProperty(value = "订单号")
    private String inv;

    @ApiModelProperty(value = "金额")
    private String amt;

    @ApiModelProperty(value = "通道商户号")
    private String merID;

    @ApiModelProperty(value = "code")
    private String refCode;

    @ApiModelProperty(value = "签名")
    private String mark;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "交易状态")
    private String result;
}
