package com.asianwallets.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "认证输入实体", description = "认证输入实体")
public class AuthenticationRequest {

    @NotNull(message = "50002")
    @ApiModelProperty("用户名")
    private String username;

    @NotNull(message = "50002")
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("系统ID")
    private String sysId;

    @ApiModelProperty("设备编号")
    private String imei;

}
