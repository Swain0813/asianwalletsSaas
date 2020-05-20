package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "merchant_report")
@ApiModel(value = "商户报备", description = "商户报备")
public class MerchantReport extends BaseEntity {

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "国家代码")
    @Column(name = "country_code")
    private String countryCode;

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "通道MCC编码")
    @Column(name = "channel_mcc")
    private String channelMcc;

    @ApiModelProperty(value = "通道子商户编号")
    @Column(name = "sub_merchant_code")
    private String subMerchantCode;

    @ApiModelProperty(value = "通道子商户名称")
    @Column(name = "sub_merchant_name")
    private String subMerchantName;

    @ApiModelProperty(value = "通道店铺名")
    @Column(name = "shop_name")
    private String shopName;

    @ApiModelProperty(value = "通道店铺编号")
    @Column(name = "shop_code")
    private String shopCode;

    @ApiModelProperty(value = "subAppid")
    @Column(name = "sub_appid")
    private String subAppid;

    @ApiModelProperty(value = "网站类型")
    @Column(name = "site_type")//APP WEB
    private String siteType;

    @ApiModelProperty(value = "网站url")
    @Column(name = "site_url")
    private String siteUrl;

    @ApiModelProperty(value = "报备状态")
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