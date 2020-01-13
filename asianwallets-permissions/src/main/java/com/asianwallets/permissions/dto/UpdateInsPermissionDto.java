package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "运营后台修改机构权限DTO", description = "运营后台修改机构权限DTO")
public class UpdateInsPermissionDto {

    @ApiModelProperty(value = "机构ID")
    private String institutionId;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "启用权限id集合")
    private List<String> openIdList;

    @ApiModelProperty(value = "禁用权限id集合")
    private List<String> offIdList;
}