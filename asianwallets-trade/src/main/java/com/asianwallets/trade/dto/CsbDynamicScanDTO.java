package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "线下同机构CSB动态扫码输入实体", description = "线下同机构CSB动态扫码输入实体")
public class CsbDynamicScanDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单时间")
    private String orderTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "设备编号")
    private String imei;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "token")
    private String token;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

}
