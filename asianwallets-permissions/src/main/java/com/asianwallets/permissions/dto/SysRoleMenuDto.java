package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "角色权限关联实体", description = "角色权限关联实体")
public class SysRoleMenuDto {

    @ApiModelProperty(value = "角色名字")
    private String roleName;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "角色描述")
    private String description;

    @ApiModelProperty(value = "权限Id")
    private List<String> menuIdList;

    @ApiModelProperty(value = "系统ID")
    private String sysId;

    @ApiModelProperty(value = "角色Id")
    private String roleId;

}
