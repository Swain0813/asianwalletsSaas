package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "三级权限输入实体", description = "三级权限输入实体")
public class ThreeMenuDto {

    @ApiModelProperty(value = "英文名称")
    private String eName;

    @ApiModelProperty(value = "中文名称")
    private String cName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;
}
