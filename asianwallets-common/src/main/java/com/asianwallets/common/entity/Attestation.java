package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "attestation")
@ApiModel(value = "密钥实体", description = "密钥数据")
public class Attestation extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "平台公钥")
    @Column(name = "pubkey")
    private String pubkey;

    @ApiModelProperty(value = "平台私钥")
    @Column(name = "prikey")
    private String prikey;

    @ApiModelProperty(value = "平台md5key")
    @Column(name = "md5key")
    private String md5key;

    @ApiModelProperty(value = "商户公钥")
    @Column(name = "mer_pubkey")
    private String merPubkey;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

}