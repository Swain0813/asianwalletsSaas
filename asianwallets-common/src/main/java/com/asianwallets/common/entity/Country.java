package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "country")
@ApiModel(value = "国家", description = "国家")
public class Country extends BaseEntity {

    @ApiModelProperty(value = "父id")
    @Column(name = "parent_id")
    private String parentId;

    @ApiModelProperty(value = "国家编号")
    @Column(name = "area_code")
    private String areaCode;

    @ApiModelProperty(value = "国家名称")
    @Column(name = "country_name")
    private String countryName;

    @ApiModelProperty(value = "地区名称")
    @Column(name = "area_name")
    private String areaName;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "二位代码")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "三位代码")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "数字代码")
    @Column(name = "extend4")
    private String extend4;

    @ApiModelProperty(value = "备注5")
    @Column(name = "extend5")
    private String extend5;

    @ApiModelProperty(value = "备注6")
    @Column(name = "extend6")
    private String extend6;
}