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
@Table(name = "bank_issuerid")
@ApiModel(value = "银行Issuerid对照表", description = "银行Issuerid对照表")
public class BankIssuerid extends BaseEntity{

    @ApiModelProperty(value = "银行名称 （通道名）")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "issuerId")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "currency")
    @Column(name = "currency")
    private String currency;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

}