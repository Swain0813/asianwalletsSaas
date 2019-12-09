package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "bank")
@ApiModel(value = "银行", description = "银行")
public class Bank extends BaseEntity {

    @ApiModelProperty(value = "银行编号")
    @Column(name = "bank_code")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "国家")
    @Column(name = "bank_country")
    private String bankCountry;

    @ApiModelProperty(value = "币种")
    @Column(name = "bank_currency")
    private String bankCurrency;

    @ApiModelProperty(value = "银行机构编号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "银行logo")
    @Column(name = "bank_img")
    private String bankImg;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

}