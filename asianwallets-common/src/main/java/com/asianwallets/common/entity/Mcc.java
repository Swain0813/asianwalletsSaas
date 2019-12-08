package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "mcc")
@ApiModel(value = "mcc", description = "mcc")
public class Mcc extends BaseEntity {

    @ApiModelProperty(value = "名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "中文")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "编号")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "备注1")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "备注2")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "备注3")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "备注4")
    @Column(name = "extend4")
    private String extend4;

    @ApiModelProperty(value = "备注5")
    @Column(name = "extend5")
    private String extend5;

    @ApiModelProperty(value = "备注6")
    @Column(name = "extend6")
    private String extend6;
}