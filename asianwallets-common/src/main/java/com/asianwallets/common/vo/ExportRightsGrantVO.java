package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "导出权益发放管理输出参数", description = "导出权益发放管理输出参数")
public class ExportRightsGrantVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "团购号")
    private String dealId;

    @ApiModelProperty(value = "分销平台")
    private String systemName;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "活动主题")
    private String activityTheme;

    @ApiModelProperty(value = "权益类型")//1-满减  2-折扣 3-套餐 4-定额
    private Byte rightsType;

    @ApiModelProperty(value = "活动数量")
    private Integer activityAmount;

    @ApiModelProperty(value = "领取数量")
    private Integer getAmount;

    @ApiModelProperty(value = "核销数量")
    private Integer cancelVerificationAmount;

    @ApiModelProperty(value = "待领取数量")
    private Integer surplusAmount;

    @ApiModelProperty(value = "活动周期")
    private String activityTime;

    @ApiModelProperty(value = "备注")
    private String remark;


}
