package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "mcc", description = "mcc")
public class MccDTO extends BasePageHelper {

    @ApiModelProperty(value = "id")
    public String id;

    @ApiModelProperty(value = "MCC名称")
    private String name;

    @ApiModelProperty(value = "MCC编号")
    private String code;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "备注1")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    private String extend2;

    @ApiModelProperty(value = "备注3")
    private String extend3;

    @ApiModelProperty(value = "备注4")
    private String extend4;

    @ApiModelProperty(value = "备注5")
    private String extend5;

    @ApiModelProperty(value = "备注6")
    private String extend6;

}