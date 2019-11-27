package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "权限输入实体", description = "输入实体")
public class SysMenuDto {

    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "父级Id")
    private String parentId;

    @ApiModelProperty(value = "英文名称")
    private String enName;

    @ApiModelProperty(value = "中文名称")
    private String cnName;

    @ApiModelProperty(value = "权限类型(1-运营,2-机构,3-商户,4-代理商,5-pos机)")
    private Integer permissionType;

    @ApiModelProperty(value = "路径")
    private String url;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "层级")
    private Integer level;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "权重")
    private Integer sort;

}