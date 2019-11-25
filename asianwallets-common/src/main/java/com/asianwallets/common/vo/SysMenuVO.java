package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-01-10 17:27
 **/
@Data
@ApiModel(value = "权限VO", description = "权限VO")
public class SysMenuVO {

    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "英文名")
    private String eName;

    @ApiModelProperty(value = "中文名")
    private String cName;

    @ApiModelProperty(value = "url")
    private String url;

}
