package com.asianwallets.permissions.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "一级权限实体", description = "一级权限实体")
public class FirstMenuVO {

    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "英文名称")
    private String eName;

    @ApiModelProperty(value = "中文名称")
    private String cName;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否选中")
    private Boolean flag = false;

    @ApiModelProperty(value = "二级权限集合")
    private List<SecondMenuVO> secondMenuVOS;
}
