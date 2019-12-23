package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "线下登录输入参数实体", description = "线下登录输入参数实体")
public class OfflineLoginDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "设备编号")
    private String imei;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "登录密码")
    private String password;
}
