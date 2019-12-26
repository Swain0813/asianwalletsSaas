package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shenxinran
 * @Date: 2019/4/4 15:36
 * @Description: 收银台输入实体
 */
@Data
@ApiModel(value = "收银台输入实体", description = "收银台输入实体")
public class CashierDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单金额")
    private String orderAmount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "交易金额")
    private String tradeAmount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单币种转交易币种 换汇汇率")
    private String exchangeRate;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "交易币种转订单币种 换汇汇率")
    private String reverseRate;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "原始汇率")
    private String originalRate;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "换汇时间")
    private String exchangeTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单号")
    private String orderId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "银行机构代码")
    private String issuerId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "支付方式")
    private String payType;

}
