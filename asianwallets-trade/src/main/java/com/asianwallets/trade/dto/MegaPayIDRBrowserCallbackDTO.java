package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/5/29 16:08
 * @Description: megaPayIDR浏览器回调实体
 */
@Data
@ApiModel(value = "megaPayIDR浏览器回调实体", description = "megaPayIDR浏览器回调实体")
public class MegaPayIDRBrowserCallbackDTO {

    @ApiModelProperty(value = "商户id")
    private String e_merID;

    @ApiModelProperty(value = "订单金额")
    private String e_amt;

    @ApiModelProperty(value = "交易结果")
    private String result;

    @ApiModelProperty(value = "订单id")
    private String e_inv;

}
