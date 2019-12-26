package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author XuWenQi
 * @Date: 2019/3/18 15:53
 * @Description: 换汇计算输入实体
 */
@Data
@ApiModel(value = "换汇计算输入实体", description = "换汇计算输入实体")
public class CalcRateDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantCode;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "支付方式")
    private String payType;


}
