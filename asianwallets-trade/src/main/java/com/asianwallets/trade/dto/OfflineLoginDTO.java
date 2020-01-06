package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "线下登录输入参数实体", description = "线下登录输入参数实体")
public class OfflineLoginDTO {

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "设备编号")
    private String imei;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "登录密码")
    private String password;
}
