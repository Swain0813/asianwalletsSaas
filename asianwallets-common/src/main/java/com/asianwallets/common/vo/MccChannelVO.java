package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "MccChannel", description = "MccChannel")
public class MccChannelVO {

    @ApiModelProperty(value = "ID")
    public String id;

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

    @ApiModelProperty(value = "通道id")
    private String cid;

    @ApiModelProperty(value = "mcc id")
    private String mid;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "mcc名称")
    private String mName;

    @ApiModelProperty(value = "mcc编号")
    private String mCode;

    @ApiModelProperty(value = "通道名称")
    private String cName;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "通道MCC编号")
    private String code;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "启用禁用Str 导出用")
    private String enabledStr;

}