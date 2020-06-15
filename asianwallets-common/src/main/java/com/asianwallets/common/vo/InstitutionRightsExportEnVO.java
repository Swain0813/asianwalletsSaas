package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author coco
 */
@Data
@ApiModel(value = "机构权益导出英文版VO", description = "机构权益导出英文版VO")
public class InstitutionRightsExportEnVO {

    @ApiModelProperty(value = "Create Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "Batch No")
    private String batchNo;

    @ApiModelProperty(value = "Institution Id")
    private String institutionId;

    @ApiModelProperty(value = "Institution Name")
    private String institutionName;

    @ApiModelProperty(value = "Merchant Id")
    private String merchantId;

    @ApiModelProperty(value = "Merchant Name")
    private String merchantName;

    @ApiModelProperty(value = "Rights Type")
    private Byte rightsType;

    @ApiModelProperty(value = "Activity Theme")
    private String activityTheme;

    @ApiModelProperty(value = "Activity Amount")
    private Integer activityAmount;

    @ApiModelProperty(value = "Activity Time")
    private String activityTime;

    @ApiModelProperty(value = "Usage Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date useTime;

    @ApiModelProperty(value = "Package Price")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "Ticket Amount")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "Deduction Amount")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "Full Deduction")
    private BigDecimal fullReductionAmount;

    @ApiModelProperty(value = "Deduction Rate")
    private BigDecimal discount;

    @ApiModelProperty(value = "Capped Amount")
    private BigDecimal capAmount;

    @ApiModelProperty(value = "Restrictions")
    private Byte getLimit;

    @ApiModelProperty(value = "Available Store Address")
    private String shopAddresses;

    @ApiModelProperty(value = "Package text")
    private String setText;

    @ApiModelProperty(value = "Rule Description")
    private String ruleDescription;

    @ApiModelProperty(value = "Equity Currency")
    private String rightsCurrency;

    @ApiModelProperty(value = "Unavailable Time")
    private String extend1;

    @ApiModelProperty(value = "Update Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "Status")
    private Boolean enabled;

    @ApiModelProperty(value = "Remark")
    private String remark;
}