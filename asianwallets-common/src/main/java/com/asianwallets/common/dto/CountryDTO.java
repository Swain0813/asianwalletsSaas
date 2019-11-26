package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "国家", description = "国家")
public class CountryDTO extends BasePageHelper {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "国家区号")
    private String areaCode;

    @ApiModelProperty(value = "英语国家")
    private String enCountry;

    @ApiModelProperty(value = "英语省份")
    private String enState;

    @ApiModelProperty(value = "英语城市")
    private String enCity;

    @ApiModelProperty(value = "国家")
    private String cnCountry;

    @ApiModelProperty(value = "省份")
    private String cnState;

    @ApiModelProperty(value = "城市")
    private String cnCity;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

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