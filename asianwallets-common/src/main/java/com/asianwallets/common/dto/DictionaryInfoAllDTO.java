package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 数据字典的输入参数
 */
@Data
@ApiModel(value = "数据字典的输入参数", description = "数据字典的输入参数")
public class DictionaryInfoAllDTO {

    @ApiModelProperty(value = "字典信息Id")
    private String id;

    @ApiModelProperty(value = "字典信息对应的类型code")
    private String dictypeCode;

    /**
     * 这个参数是选择的类型
     */
    @ApiModelProperty(value = "类型")
    private String choseType;

    /**
     * 这个参数代表要添加的类型
     */
    @ApiModelProperty(value = "类型名称")
    private String type;

    @ApiModelProperty(value = "字典信息code")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "语种code")
    private String language;

    @ApiModelProperty(value = "币种默认值")
    private String defaultValue;
}
