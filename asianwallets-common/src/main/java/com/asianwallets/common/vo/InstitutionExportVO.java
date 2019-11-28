package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-28 13:45
 **/
@Data
public class InstitutionExportVO {

    @ApiModelProperty(value = "机构编号")
    public String id;

    /**
     * 机构中文名称
     */
    @ApiModelProperty(value = "机构中文名称")
    private String cnName;

    /**
     * 所属国家
     */
    @ApiModelProperty(value = "所属国家")
    private String country;
    /**
     * 公司注册号
     */
    @ApiModelProperty(value = "公司注册号")
    private String companyRegistNumber;
    /**
     * 国家区号
     */
    @ApiModelProperty(value = "国家区号")
    private String countryCode;
    /**
     * 拓展销售
     */
    @ApiModelProperty(value = "拓展销售")
    private String developSales;
    /**
     * 机构电话
     */
    @ApiModelProperty(value = "机构电话")
    private String institutionPhone;
    /**
     * 法人姓名
     */
    @ApiModelProperty(value = "法人姓名")
    private String legalName;
    /**
     * 机构邮编
     */
    @ApiModelProperty(value = "机构邮编")
    private String institutionPostalCode;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String institutionEmail;

    @ApiModelProperty(value = "机构网站url")
    private String institutionWebUrl;
    
    /**
     * 联系人地址
     */
    @ApiModelProperty(value = "联系人地址")
    private String contactAddress;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contactPeople;
    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    /**
     * 审核状态 1-待审核 2-审核通过 3-审核不通过
     */
    @ApiModelProperty(value = "审核状态")
    private Byte auditStatus;

    /**
     * 启用禁用
     */
    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;
}
