package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

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

    public MccChannelExportVO(Date createTime, String mCode, String mName, String cName, String code, String modifier, Date updateTime, String creator, String enabledStr) {
        this.createTime = createTime;
        this.mCode = mCode;
        this.mName = mName;
        this.cName = cName;
        this.code = code;
        this.modifier = modifier;
        this.updateTime = updateTime;
        this.creator = creator;
        this.enabledStr = enabledStr;
    }

    public MccChannelExportVO() {
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEnabledStr() {
        return enabledStr;
    }

    public void setEnabledStr(String enabledStr) {
        this.enabledStr = enabledStr;
    }
}