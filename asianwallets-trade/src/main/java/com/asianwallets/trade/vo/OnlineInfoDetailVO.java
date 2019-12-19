package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "线上查询关联数据vo", description = "线上查询关联数据vo")
public class OnlineInfoDetailVO {

    @ApiModelProperty("通道code")
    private String channelCode;

    @ApiModelProperty("merchant_product id")
    private String merProId;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("产品code")
    private Integer productCode;
}