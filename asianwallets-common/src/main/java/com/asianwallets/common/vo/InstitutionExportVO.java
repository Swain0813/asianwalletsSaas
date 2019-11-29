package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-28 13:45
 **/
@Data
public class InstitutionExportVO {

    @ApiModelProperty(value = "机构编号")
    public String id;

    @ApiModelProperty(value = "机构中文名称")
    private String cnName;

    @ApiModelProperty(value = "所属国家")
    private String country;

    @ApiModelProperty(value = "拓展销售")
    private String developSales;

    @ApiModelProperty(value = "公司注册号")
    private String companyRegistNumber;


    @ApiModelProperty(value = "公司证件有效期")
    private String companyValidity;

    @ApiModelProperty(value = "法人证件编号")
    private String legalPassportCode;

    @ApiModelProperty(value = "法人证件有效期")
    private String legalPassportValidity;

    @ApiModelProperty(value = "国家区号")
    private String countryCode;

    @ApiModelProperty(value = "法人姓名")
    private String legalName;

    @ApiModelProperty(value = "机构地址")
    private String institutionAdress;

    @ApiModelProperty(value = "机构电话")
    private String institutionPhone;

    @ApiModelProperty(value = "机构联系人")
    private String contactPeople;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "商务邮箱")
    private String institutionEmail;

    @ApiModelProperty(value = "机构网站url")
    private String institutionWebUrl;


    /**
     * 审核状态 1-待审核 2-审核通过 3-审核不通过
     */
    @ApiModelProperty(value = "审核状态")
    private Byte auditStatus;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "审核人")
    private String modifier;

    @ApiModelProperty(value = "审核备注")
    @Column(name = "remark")
    private String remark;
}
