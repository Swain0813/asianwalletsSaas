package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "导出票券VO", description = "导出票券VO")
public class ExportRightsUserGrantVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "团购编号")
    private String dealId;

    @ApiModelProperty(value = "平台订单流水号")
    private String systemOrderId;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "权益类型")// 1-满减  2-折扣 3-套餐
    private Byte rightsType;

    @ApiModelProperty(value = "票面金额")
    private BigDecimal distributionPrice;

    @ApiModelProperty(value = "分销平台")
    private String systemName;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "票券状态")// 1-待领取 2-未使用 3-已使用 4-已过期 5-已退款
    private Byte ticketStatus;

    @ApiModelProperty(value = "备注")
    private String remark;
}
