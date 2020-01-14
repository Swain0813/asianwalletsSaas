package com.asianwallets.permissions.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "三级权限实体", description = "三级权限实体")
public class ThreeMenuVO {

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
}
