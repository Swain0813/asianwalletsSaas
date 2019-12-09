package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * MCC映射表
 */
@Data
@Table(name = "mcc_channel")
@ApiModel(value = "MccChannel", description = "MccChannel")
public class MccChannel extends BaseEntity {

    @ApiModelProperty(value = "mcc id")
    @Column(name = "mid")
    private String mid;

    @ApiModelProperty(value = "通道的id")
    @Column(name = "cid")
    private String cid;

    @ApiModelProperty(value = "通道MCC编码")
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

    @ApiModelProperty(value = "备注7")
    @Column(name = "extend7")
    private String extend7;

    @ApiModelProperty(value = "备注8")
    @Column(name = "extend8")
    private String extend8;

    @ApiModelProperty(value = "备注9")
    @Column(name = "extend9")
    private String extend9;

    @ApiModelProperty(value = "备注10")
    @Column(name = "extend10")
    private String extend10;
}