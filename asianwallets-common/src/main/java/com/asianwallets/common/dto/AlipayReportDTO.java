package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "支付宝二级商户报备DTO", description = "支付宝二级商户报备DTO")
public class AlipayReportDTO {

    @ApiModelProperty(value = "服务名称")
    private String service = "alipay.overseas.secmerchant.online.maintain";

    @ApiModelProperty(value = "通道商户号")
    private String partner;

    @ApiModelProperty(value = "请求数据编码所使用的字符集")
    private String _input_charset = "UTF-8";

    @ApiModelProperty(value = "系统发送请求的时间")
    private String timestamp;

    @ApiModelProperty(value = "商户进件时的商户名称")
    private String secondary_merchant_name;

    @ApiModelProperty(value = "我司为商户分配的商户唯一编号")
    private String secondary_merchant_id;

    @ApiModelProperty(value = "通道MCC编码")
    private String secondary_merchant_industry;

    @ApiModelProperty(value = "国家二位代码")
    private String register_country;

    @ApiModelProperty(value = "商户地址")
    private String register_address;

    @ApiModelProperty(value = "商户类型：个人企业传NDIVIDUAL、非个人企业ENTERPRISE")
    private String secondary_merchant_type;

    @ApiModelProperty(value = "json信息")
    private String site_infos;

    @ApiModelProperty(value = "公司注册号")
    private String registration_no;

    @ApiModelProperty(value = "法人姓名")
    private String shareholder_name;

    @ApiModelProperty(value = "法人证件编号")
    private String shareholder_id;

    @ApiModelProperty(value = "法人姓名")
    private String representative_name;

    @ApiModelProperty(value = "法人证件编号")
    private String representative_id;

    @ApiModelProperty(value = "签名类型")
    private String sign_type;

    @ApiModelProperty(value = "签名")
    private String sign;
}
