package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "pay_type")
@ApiModel(value = "支付方式", description = "支付方式")
public class PayType extends BaseEntity {

    @ApiModelProperty(value = "名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "中文")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "大图标")
    @Column(name = "picon")
    private String picon;

    @ApiModelProperty(value = "小图标")
    @Column(name = "dicon")
    private String dicon;

    @ApiModelProperty(value = "付款类型 0 收 1 付")
    @Column(name = "mode")
    private Byte mode;

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