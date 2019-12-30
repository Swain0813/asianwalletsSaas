package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/6/05 15:08
 * @Description: megaPayIDR服务器回调实体
 */
@Data
@ApiModel(value = "megaPayIDR服务器回调实体", description = "megaPayIDR服务器回调实体")
public class MegaPayIDRServerCallbackDTO {

    @ApiModelProperty(value = "交易状态")
    private String result;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "订单号")
    private String np_inv;

    @ApiModelProperty(value = "金额")
    private String np_amt;

    @ApiModelProperty(value = "通道商户号")
    private String np_merID;

    @ApiModelProperty(value = "code")
    private String np_refCode;

    @ApiModelProperty(value = "签名")
    private String np_mark;
}
