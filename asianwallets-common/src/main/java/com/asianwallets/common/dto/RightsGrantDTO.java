package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "权益发放管理查询输入参数", description = "权益发放管理查询输入参数")
public class RightsGrantDTO extends BasePageHelper {

    @ApiModelProperty(value = "权益发放管理表id")
    private String id;

    @ApiModelProperty(value = "团购号")
    private String dealId;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "发放平台")
    private String systemName;

    @ApiModelProperty(value = "权益类型")//1-满减  2-折扣 3-套餐
    private Byte rightsType;

    @ApiModelProperty(value = "票券状态")//1-待领取 2-未使用 3-已使用 4-已过期 5-已退款
    private Byte ticketStatus;

    @ApiModelProperty(value = "启用禁用")
    private Byte enabled;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

}
