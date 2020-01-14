package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "权限dto", description = "权限dto")
public class UpdateInsPermissionDto {

    @ApiModelProperty(value = "机构ID")
    private String institutionId;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "启用权限id集合")
    private List<String> openIdList;

//    @ApiModelProperty(value = "禁用权限id集合")
//    private List<String> offIdList;
}