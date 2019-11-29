package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "权限输入实体", description = "输入实体")
public class SysMenuDto {

    @ApiModelProperty(value = "权限ID")
    private String menuId;

    @ApiModelProperty(value = "父级ID")
    private String parentId;

    @ApiModelProperty(value = "层级")
    private Integer level;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "英文名称")
    private String enName;

    @ApiModelProperty(value = "中文名称")
    private String cnName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;
}