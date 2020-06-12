package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "权益发放详情VO", description = "权益发放详情VO")
public class RightsUserGrantDetailVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "团购号")
    private String dealId;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "平台名称")
    private String systemName;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "二级商户编号")
    private String merchantId;

    @ApiModelProperty(value = "二级商户名称")
    private String merchantName;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "权益类型 1-满减  2-折扣 3-套餐")
    private Byte rightsType;

    @ApiModelProperty(value = "票券状态 1-待领取 2-未使用 3-已使用 4-已过期 5-已退款")
    private Byte ticketStatus;

    @ApiModelProperty(value = "备注")
    private String remark;

    //-----------------------【票券购买信息】------------------------
    @ApiModelProperty(value = "发放平台返回的交易流水号")
    private String systemOrderId;

    @ApiModelProperty(value = "用户手机号")
    private String mobileNo;

    @ApiModelProperty(value = "权益币种")
    private String rightsCurrency;

    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "票券购买价")
    private BigDecimal ticketBuyPrice;

    @ApiModelProperty(value = "权益发放批次号")
    private String batchNo;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "套餐金额")
    private BigDecimal packageValue;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "扣率")
    private BigDecimal discount;


    //-----------------------【票券核销信息】------------------------
    @ApiModelProperty(value = "核销时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date verifyTime;

    @ApiModelProperty(value = "和小订单号")
    private String verifyOrderId;

    @ApiModelProperty(value = "核销金额")
    private BigDecimal cancelVerificationAmount;

    @ApiModelProperty(value = "核销币种")
    private String verifyCurrency;

    @ApiModelProperty(value = "核销状态 1.核销中 2.核销成功 3.核销失败")
    private Byte verifyStatus;

    @ApiModelProperty(value = "核销备注")
    private String verifyRemark;
}
