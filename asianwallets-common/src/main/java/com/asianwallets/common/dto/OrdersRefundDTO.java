package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "退款订单输入实体", description = "退款订单输入实体")
public class OrdersRefundDTO extends BasePageHelper {

    @ApiModelProperty(value = "退款币种")
    private String refundCurrency;

    @ApiModelProperty(value = "退款订单号")
    private String id;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "机构编号")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "退款状态 1:退款中 2:退款成功 3:退款失败 4:系统创建失败")
    private Byte refundStatus;

    @ApiModelProperty(value = "退款申请开始时间")
    private String startDate;

    @ApiModelProperty(value = "退款申请交易结束时间")
    private String endDate;

}
