package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;


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

    public MccChannelVO() {
    }

    public MccChannelVO(String id, Date createTime, Date updateTime, String creator, String modifier, String cid, String mid, String remark, String mName, String mCode, String cName, String language, String code, Boolean enabled, String enabledStr) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.creator = creator;
        this.modifier = modifier;
        this.cid = cid;
        this.mid = mid;
        this.remark = remark;
        this.mName = mName;
        this.mCode = mCode;
        this.cName = cName;
        this.language = language;
        this.code = code;
        this.enabled = enabled;
        this.enabledStr = enabledStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEnabledStr() {
        return enabledStr;
    }

    public void setEnabledStr(String enabledStr) {
        this.enabledStr = enabledStr;
    }
}