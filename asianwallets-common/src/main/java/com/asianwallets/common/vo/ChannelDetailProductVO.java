package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查询通道响应关联产品输出实体", description = "查询通道响应关联产品输出实体")
public class ChannelDetailProductVO {

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "交易方向")
    private Byte transType;

    @ApiModelProperty(value = "支付类型")
    private String payType;

    @ApiModelProperty(value = "币种")
    private String currency;

}
