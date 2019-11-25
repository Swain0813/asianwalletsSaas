package com.asianwallets.permissions.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件对象实体
 */
@Data
@ApiModel(value = "文件对象实体", description = "文件对象实体")
public class FileInfo {

    @ApiModelProperty(value = "旧文件名")
    private String oldName;

    @ApiModelProperty(value = "新文件名")
    private String newName;

    @ApiModelProperty(value = "文件地址")
    private String path;
}
