package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "线下查询产品输出实体", description = "线下查询产品输出实体")
public class PosMerProVO {

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "交易类型")
    private Byte transType;

    @ApiModelProperty(value = "支付方式名称")
    private String payTypeName;

    @ApiModelProperty(value = "产品详情图片")
    private String productDetailsLogo;

    @ApiModelProperty(value = "产品打印图片")
    private String productPrintLogo;

    @ApiModelProperty(value = "支付方式标记")
    private String flag;
}
