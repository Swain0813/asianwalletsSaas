package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-28 14:07
 **/
@Data
public class MerchantExportVO {

    @ApiModelProperty(value = "创建时间")
    public Date createTime;


    @ApiModelProperty(value = "商户编号")
    public String id;

    /**
     * 商户中文名称
     */
    @ApiModelProperty(value = "商户名称")
    private String cnName;
    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String ext5;

    /**
     * 商户类型
     */
    @ApiModelProperty(value = "商户类型")
    private String merchantType;

    /**
     * 集团主账户
     */
    @ApiModelProperty(value = "集团主账户")
    private String groupMasterAccount;


    @ApiModelProperty(value = "代理商编号")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    private String ext6;

    @ApiModelProperty(value = "国家")
    private String country;

    /**
     * 拓展销售
     */
    @ApiModelProperty(value = "拓展销售")
    private String developSales;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;
    ///**
    // * 地区
    // */
    //@ApiModelProperty(value = "地区")
    //private String region;
    /**
     * mcc
     */
    @ApiModelProperty(value = "mcc")
    private String mcc;



    ///**
    // * 公司注册号
    // */
    @ApiModelProperty(value = "公司注册号")
    private String companyRegistNumber;
    ///**
    // * 国家区号
    // */
    //@ApiModelProperty(value = "国家区号")
    //private String countryCode;
    ///**
    // * 机构电话
    // */
    @ApiModelProperty(value = "机构电话")
    private String institutionPhone;
    ///**
    // * 法人姓名
    // */
    @ApiModelProperty(value = "法人姓名")
    private String legalName;
    ///**
    // * 邮编
    // */
    @ApiModelProperty(value = "邮编")
    private String merchantPostalCode;
    ///**
    // * 邮箱
    // */
    @ApiModelProperty(value = "邮箱")
    private String merchantEmail;
    ///**
    // * 联系人地址
    // */
    //@ApiModelProperty(value = "联系人地址")
    //private String contactAddress;
    ///**
    // * 联系人
    // */
    @ApiModelProperty(value = "联系人")
    private String contactPeople;
    ///**
    // * 联系人电话
    // */
    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;
    ///**
    // * 审核状态 1-待审核 2-审核通过 3-审核不通过
    // */
    //@ApiModelProperty(value = "审核状态 1-待审核 2-审核通过 3-审核不通过")
    //private Byte auditStatus;


}
