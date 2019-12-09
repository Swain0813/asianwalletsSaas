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
@Table(name = "bank_issuer_id")
@ApiModel(value = "银行与银行机构代码映射表", description = "银行与银行机构代码映射表")
public class BankIssuerId extends BaseEntity {

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "银行机构号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "币种")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

}