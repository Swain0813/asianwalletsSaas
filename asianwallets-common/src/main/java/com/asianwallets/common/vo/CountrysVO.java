package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CountryVO
 * @Description 国家输出VO
 * @Author abc
 * @Date 2019/11/25 14:23
 * @Version 1.0
 */
@Data
@ApiModel(value = "国家输出VO", description = "国家输出VO")
public class CountrysVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "国家区号")
    private String areaCode;

    @ApiModelProperty(value = "英语国家")
    private String enCountry;

    @ApiModelProperty(value = "中文国家")
    private String cnCountry;

    @ApiModelProperty(value = "英语地区")
    private String enArea;

    @ApiModelProperty(value = "中文地区")
    private String cnArea;

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

}



