package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "修改密码实体", description = "修改密码实体")
public class UpdatePasswordDto {

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "新密码")
    private String password;

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;
}
