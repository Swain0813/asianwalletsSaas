package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @author yx
 * @since 2019-11-25
 */
@Data
@Entity
@Table(name = "merchant")
public class Merchant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "代理商id")
    @Column(name = "agent_id")
    private String agentId;

    /**
     * 上级商户id
     */
    @ApiModelProperty(value = "上级商户id")
    @Column(name = "parent_id")
    private String parentId;
    /**
     * 商户中文名称
     */
    @ApiModelProperty(value = "商户中文名称")
    @Column(name = "cn_name")
    private String cnName;
    /**
     * 商户英文名称
     */
    @ApiModelProperty(value = "商户英文名称")
    @Column(name = "en_name")
    private String enName;
    /**
     * 国别类型 0 境外 1 中国境内
     */
    @ApiModelProperty(value = "国别类型 0 境外 1 中国境内")
    @Column(name = "country_type")
    private Integer countryType;

    @ApiModelProperty(value = "国家")
    @Column(name = "country")
    private String country;
    /**
     * 地区
     */
    @ApiModelProperty(value = "地区")
    @Column(name = "region")
    private String region;
    /**
     * mcc
     */
    @ApiModelProperty(value = "mcc")
    @Column(name = "mcc")
    private String mcc;
    /**
     * 商户类型
     */
    @ApiModelProperty(value = "商户类型")
    @Column(name = "merchant_type")
    private String merchantType;
    /**
     * 集团主账户
     */
    @ApiModelProperty(value = "集团主账户")
    @Column(name = "group_master_account")
    private String groupMasterAccount;
    /**
     * 拓展销售
     */
    @ApiModelProperty(value = "拓展销售")
    @Column(name = "develop_sales")
    private String developSales;
    /**
     * 公司注册号
     */
    @ApiModelProperty(value = "公司注册号")
    @Column(name = "company_regist_number")
    private String companyRegistNumber;
    /**
     * 公司证件有效期
     */
    @ApiModelProperty(value = "公司证件有效期")
    @Column(name = "company_validity")
    private String companyValidity;
    /**
     * 法人证件编号
     */
    @ApiModelProperty(value = "法人证件编号")
    @Column(name = "legal_passport_code")
    private String legalPassportCode;
    /**
     * 证件有效期
     */
    @ApiModelProperty(value = "证件有效期")
    @Column(name = "legal_passport_validity")
    private String legalPassportValidity;
    /**
     * 国家区号
     */
    @ApiModelProperty(value = "国家区号")
    @Column(name = "country_code")
    private String countryCode;
    /**
     * 机构地址
     */
    @ApiModelProperty(value = "机构地址")
    @Column(name = "institution_adress")
    private String institutionAdress;
    /**
     * 法人证件照片
     */
    @ApiModelProperty(value = "法人证件照片")
    @Column(name = "legal_passport_img")
    private String legalPassportImg;
    /**
     * 机构协议
     */
    @ApiModelProperty(value = "机构协议")
    @Column(name = "institution_contract")
    private String institutionContract;
    /**
     * 公司章程
     */
    @ApiModelProperty(value = "公司章程")
    @Column(name = "company_articles")
    private String companyArticles;
    /**
     * 企业证件
     */
    @ApiModelProperty(value = "企业证件")
    @Column(name = "business_certificate")
    private String businessCertificate;
    /**
     * 机构电话
     */
    @ApiModelProperty(value = "机构电话")
    @Column(name = "institution_phone")
    private String institutionPhone;
    /**
     * 法人姓名
     */
    @ApiModelProperty(value = "法人姓名")
    @Column(name = "legal_name")
    private String legalName;
    /**
     * 经营类目
     */
    @ApiModelProperty(value = "经营类目")
    @Column(name = "business_category")
    private String businessCategory;
    /**
     * 行业许可
     */
    @ApiModelProperty(value = "行业许可")
    @Column(name = "business_license")
    private String businessLicense;
    /**
     * 行业类别
     */
    @ApiModelProperty(value = "行业类别")
    @Column(name = "industry_category")
    private String industryCategory;
    /**
     * 邮编
     */
    @ApiModelProperty(value = "邮编")
    @Column(name = "merchant_postal_code")
    private String merchantPostalCode;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Column(name = "merchant_email")
    private String merchantEmail;
    /**
     * 网站url
     */
    @ApiModelProperty(value = "网站url")
    @Column(name = "merchant_web_url")
    private String merchantWebUrl;
    /**
     * 联系人地址
     */
    @ApiModelProperty(value = "联系人地址")
    @Column(name = "contact_address")
    private String contactAddress;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    @Column(name = "contact_people")
    private String contactPeople;
    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    @Column(name = "contact_phone")
    private String contactPhone;
    /**
     * 审核状态 1-待审核 2-审核通过 3-审核不通过
     */
    @ApiModelProperty(value = "审核状态 1-待审核 2-审核通过 3-审核不通过")
    @Column(name = "audit_status")
    private Integer auditStatus;
    @ApiModelProperty(value = "ext7")
    @Column(name = "ext7")
    private String ext7;

    @ApiModelProperty(value = "ext6")
    @Column(name = "ext6")
    private String ext6;

    @ApiModelProperty(value = "ext5")
    @Column(name = "ext5")
    private String ext5;

    @ApiModelProperty(value = "ext4")
    @Column(name = "ext4")
    private String ext4;

    @ApiModelProperty(value = "ext3")
    @Column(name = "ext3")
    private String ext3;

    @ApiModelProperty(value = "ext2")
    @Column(name = "ext2")
    private String ext2;

    @ApiModelProperty(value = "ext1")
    @Column(name = "ext1")
    private String ext1;

    @ApiModelProperty(value = "启用禁用")
    @Column(name = "enabled")
    private Boolean enabled;


}
