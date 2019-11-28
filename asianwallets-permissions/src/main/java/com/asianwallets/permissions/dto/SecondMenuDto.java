package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "二级权限输入实体", description = "二级权限输入实体")
public class SecondMenuDto {

    @ApiModelProperty(value = "权限ID")
    private String menuId;

    @ApiModelProperty(value = "父级ID")
    private String parentId;

    @ApiModelProperty(value = "英文名称")
    private String eName;

    @ApiModelProperty(value = "中文名称")
    private String cName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "权限类型")
    private Integer permissionType;

    @ApiModelProperty(value = "三级权限输入实体集合")
    private List<ThreeMenuDto> threeMenuDtoList;
}
