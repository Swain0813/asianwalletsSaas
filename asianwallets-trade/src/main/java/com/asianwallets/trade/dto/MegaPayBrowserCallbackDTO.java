package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/5/29 16:08
 * @Description: megaPay浏览器回调实体
 */
@Data
@ApiModel(value = "megaPay浏览器回调实体", description = "megaPay浏览器回调实体")
public class MegaPayBrowserCallbackDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "交易结果")
    private String result;

    @ApiModelProperty(value = "订单id")
    private String orderID;

}
