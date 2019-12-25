package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "订单退款详情输出实体", description = "订单退款详情输出实体")
public class OrdersDetailRefundVO {
    @ApiModelProperty(value = "退款订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderRefundCreateTime;

    @ApiModelProperty(value = "退款流水号")
    private String orderRefundId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal orderRefundAmount;

    @ApiModelProperty(value = "退款汇率")
    private BigDecimal refundExchangeRate;

    @ApiModelProperty(value = "通道退款金额")
    private BigDecimal refundChannelAmount;

    @ApiModelProperty(value = "退款交易币种")
    private String refundTradeCurrency;

    @ApiModelProperty(value = "退款状态")
    private Byte refundStatus;

    @ApiModelProperty(value = "退款完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date refundFinishTime;

    @ApiModelProperty(value = "退款手续费")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "退款备注")
    private String refundRemark;
}
