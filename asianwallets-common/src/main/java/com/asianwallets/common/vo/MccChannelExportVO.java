package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "MccChannelExportVO", description = "MccChannelExportVO")
public class MccChannelExportVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "MCC编号")
    private String mCode;

    @ApiModelProperty(value = "MCC名称")
    private String mName;

    @ApiModelProperty(value = "通道名称")
    private String cName;

    @ApiModelProperty(value = "通道MCC编号")
    private String code;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "操作人")
    private String creator;

    @ApiModelProperty(value = "状态")
    private String enabledStr;

}