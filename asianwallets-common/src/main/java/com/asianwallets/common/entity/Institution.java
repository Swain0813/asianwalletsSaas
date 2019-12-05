package com.asianwallets.common.entity;
import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 机构表
 */
@Data
@Entity
@Table(name = "institution")
@ApiModel(value = "机构表", description = "机构表")
public class Institution extends BaseEntity {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构logo")
	@Column(name = "institution_logo")
	private String institutionLogo;

	@ApiModelProperty(value = "机构中文名称")
	@Column(name = "cn_name")
	private String cnName;

	@ApiModelProperty(value = "机构英文名称")
	@Column(name = "en_name")
	private String enName;

	@ApiModelProperty(value = "国别类型")//0 境外 1 中国境内
	@Column(name = "country_type")
	private Integer countryType;

	@ApiModelProperty(value = "所属国家")
	@Column(name = "country")
	private String country;

	@ApiModelProperty(value = "公司注册号")
	@Column(name = "company_regist_number")
	private String companyRegistNumber;

	@ApiModelProperty(value = "公司证件有效期")
	@Column(name = "company_validity")
	private String companyValidity;

	@ApiModelProperty(value = "法人证件编号")
	@Column(name = "legal_passport_code")
	private String legalPassportCode;

	@ApiModelProperty(value = "法人证件有效期")
	@Column(name = "legal_passport_validity")
	private String legalPassportValidity;

	@ApiModelProperty(value = "国家区号")
	@Column(name = "country_code")
	private String countryCode;

	@ApiModelProperty(value = "机构地址")
	@Column(name = "institution_adress")
	private String institutionAdress;

	@ApiModelProperty(value = "法人证件照片")
	@Column(name = "legal_passport_img")
	private String legalPassportImg;

	@ApiModelProperty(value = "拓展销售")
	@Column(name = "develop_sales")
	private String developSales;

	@ApiModelProperty(value = "机构协议")
	@Column(name = "institution_contract")
	private String institutionContract;

	@ApiModelProperty(value = "公司章程")
	@Column(name = "company_articles")
	private String companyArticles;

	@ApiModelProperty(value = "企业证件")
	@Column(name = "business_certificate")
	private String businessCertificate;

	@ApiModelProperty(value = "机构电话")
	@Column(name = "institution_phone")
	private String institutionPhone;

	@ApiModelProperty(value = "法人姓名")
	@Column(name = "legal_name")
	private String legalName;

	@ApiModelProperty(value = "经营类目")
	@Column(name = "business_category")
	private String businessCategory;

	@ApiModelProperty(value = "行业许可")
	@Column(name = "business_license")
	private String businessLicense;

	@ApiModelProperty(value = "行业类别")
	@Column(name = "industry_category")
	private String industryCategory;

	@ApiModelProperty(value = "机构邮编")
	@Column(name = "institution_postal_code")
	private String institutionPostalCode;

	@ApiModelProperty(value = "机构邮箱")
	@Column(name = "institution_email")
	private String institutionEmail;

	@ApiModelProperty(value = "机构网站url")
	@Column(name = "institution_web_url")
	private String institutionWebUrl;

	@ApiModelProperty(value = "联系人地址")
	@Column(name = "contact_address")
	private String contactAddress;

	@ApiModelProperty(value = "联系人")
	@Column(name = "contact_people")
	private String contactPeople;

	@ApiModelProperty(value = "联系人电话")
	@Column(name = "contact_phone")
	private String contactPhone;

	@ApiModelProperty(value = "审核状态")//1-待审核 2-审核通过 3-审核不通过
	@Column(name = "audit_status")
	private Byte auditStatus;

	@ApiModelProperty(value = "是否支持退款")
	@Column(name = "support_refund_state")
	private Boolean supportRefundState;

	@ApiModelProperty(value = "退款方式")//1-API接口 2-人工退款
	@Column(name = "refund_method")
	private Byte refundMethod;

	@ApiModelProperty(value = "是否开通DCC")
	@Column(name = "dcc")
	private Boolean dcc;

	@ApiModelProperty(value = "是否开通异步通知")
	@Column(name = "asyn_notice")
	private Boolean asynNotice;

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
