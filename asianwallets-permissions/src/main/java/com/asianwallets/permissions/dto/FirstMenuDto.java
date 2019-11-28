package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "一级权限实体输入实体", description = "一级权限实体输入实体")
public class FirstMenuDto {

    @ApiModelProperty(value = "英文名称")
    private String eName;

    @ApiModelProperty(value = "中文名称")
    private String cName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "二级权限输入实体集合")
    private List<SecondMenuDto> secondMenuDtoList;
}
