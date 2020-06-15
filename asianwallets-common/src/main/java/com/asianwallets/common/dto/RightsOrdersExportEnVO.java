package com.asianwallets.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-31 10:43
 **/
@Data
public class RightsOrdersExportEnVO {


    @ApiModelProperty(value = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "Institution Id")
    private String institutionId;

    @ApiModelProperty(value = "Institution Name")
    private String institutionName;

    @ApiModelProperty(value = "Merchant Id")
    private String merchantId;

    @ApiModelProperty(value = "Merchant Name")
    private String merchantName;


    @ApiModelProperty(value = "Order No")
    private String orderNo;

    @ApiModelProperty(value = "Order Amount")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "Rights Currency")
    private String rightsCurrency;

    @ApiModelProperty(value = "Ticket Id")
    private String ticketId;

    @ApiModelProperty(value = "Ticket Amount")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "Deduction Amount")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "Actual Amount")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "Rights Type")////权益类型：1-满减 2-折扣 3-套餐 4-定额
    private String rightsType;

    @ApiModelProperty(value = "Completion Time Of Verification")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "Distribution Platform")
    private String systemName;

    /**
     * 核销状态：1.核销中 2.核销成功 3.核销失败
     */
    @ApiModelProperty(value = "status")
    private String status;


    @ApiModelProperty(value = "remark")
    private String remark;


}
