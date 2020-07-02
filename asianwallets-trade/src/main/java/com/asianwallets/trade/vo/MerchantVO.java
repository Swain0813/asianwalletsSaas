package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "静态码输入金额需要的输出参数", description = "静态码输入金额需要的输出参数")
public class MerchantVO {

    @ApiModelProperty(value = "商户logo")
    private String merchantLogo;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;
}
