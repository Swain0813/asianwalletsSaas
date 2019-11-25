package com.asianwallets.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "登录角色响应实体", description = "登录角色响应实体")
public class ResRole {

    @ApiModelProperty("角色ID")
    private String id;

    @ApiModelProperty("角色Code")
    private String roleCode;

    @ApiModelProperty("角色名称")
    private String roleName;

}
