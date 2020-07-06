package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@ApiModel(value = "线下码牌交易输入实体", description = "线下码牌交易输入实体")
public class OfflineCodeTradeDTO {

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户订单时间")
    private String orderTime;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "扫码标志")
    private String userAgent;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "聚合码编号")
    private String merchantCardId;

    @ApiModelProperty(value = "签名方式 1为RSA 2为MD5")
    private String signType;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;
}
