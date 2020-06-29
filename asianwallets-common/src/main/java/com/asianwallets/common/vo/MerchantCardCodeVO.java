package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查看商户静态码输出参数", description = "查看商户静态码输出参数")
public class MerchantCardCodeVO {

    @ApiModelProperty(value = "商户logo")
    private String merchantLogo;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "二维码的地址")
    private String qrcodeUrl;

    @ApiModelProperty(value = "静态码编号")
    private String qrcodeId;

    @ApiModelProperty(value = "产品图标")
    private String productImgs;
}
