package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "线上交易输入实体", description = "线上交易输入实体")
public class OnlineTradeDTO {

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
    @ApiModelProperty(value = "商户订单时间")
    private String orderTime;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "签名方式")//1为RSA 2为MD5
    private String signType;

    @NotNull(message = "52002")
    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;

    //----------------【非必填参数】---------------------

    @ApiModelProperty(value = "issuerId")
    private String issuerId;

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "浏览器回调地址")
    private String browserUrl;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    private String productDescription;

    @ApiModelProperty(value = "付款人账户")
    private String payerAccount;

    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "付款人银行")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    private String payerPhone;

    @ApiModelProperty(value = "付款人地址")
    private String payerAddress;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    private String remark3;
}
