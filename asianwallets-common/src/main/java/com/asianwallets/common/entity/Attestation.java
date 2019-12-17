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

    @ApiModelProperty(value = "机构ID")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "公钥")
    @Column(name = "pubkey")
    private String pubkey;

    @ApiModelProperty(value = "私钥")
    @Column(name = "prikey")
    private String prikey;

    @ApiModelProperty(value = "签名")
    @Column(name = "md5key")
    private String md5key;

    @ApiModelProperty(value = "类型")//0-机构的 1-机构对应平台的 3登录及收银台的
    @Column(name = "type")
    private Byte type;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;

}