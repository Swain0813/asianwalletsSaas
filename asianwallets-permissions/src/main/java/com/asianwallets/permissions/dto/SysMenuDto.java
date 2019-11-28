package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "权限输入实体", description = "输入实体")
public class SysMenuDto {

    @ApiModelProperty(value = "权限ID")
    private String menuId;

    @ApiModelProperty(value = "英文名称")
    private String enName;

    @ApiModelProperty(value = "中文名称")
    private String cnName;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;
}