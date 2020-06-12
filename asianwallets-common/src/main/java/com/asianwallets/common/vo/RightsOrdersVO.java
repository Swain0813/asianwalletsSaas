package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-30 14:46
 **/
@Data
public class RightsOrdersVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "机构订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "票券币种")
    private String rightsCurrency;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "票券金额")
    private BigDecimal ticketAmount;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "权益类型")//权益类型：1-满减 2-折扣 3-套餐 4-定额
    private String rightsType;

    @ApiModelProperty(value = "核销完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "分销平台")
    private String systemName;

    @ApiModelProperty(value = "核销状态")//核销状态：1.核销中 2.核销成功 3.核销失败
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;
}
