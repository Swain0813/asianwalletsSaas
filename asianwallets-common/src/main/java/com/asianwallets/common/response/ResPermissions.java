package com.asianwallets.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "登录权限响应实体", description = "登录权限响应实体")
public class ResPermissions {

    @ApiModelProperty("权限ID")
    private String id;

    @ApiModelProperty("权限中文名称")
    private String cName;

    @ApiModelProperty("权限英文名称")
    private String eName;

    @ApiModelProperty("url")
    private String url;

}
