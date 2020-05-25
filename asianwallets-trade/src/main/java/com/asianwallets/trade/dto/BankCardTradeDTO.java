package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 银行卡收单
 * @author: YangXu
 * @create: 2020-05-25 10:38
 **/
@Data
@ApiModel(value = "银行卡收单DTO", description = "银行卡收单DTO")
public class BankCardTradeDTO {


    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户订单时间")
    private String orderTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "设备编号")
    private String imei;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "Token")
    private String token;

    @ApiModelProperty(value = "线下BSC付款码")
    private String authCode;

    @NotBlank(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "签名方式 1为RSA 2为MD5")
    private String signType;

    //----------------【非必填参数】---------------------
    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;

    @ApiModelProperty(value = "浏览器回调地址")
    private String browserUrl;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    private String productDescription;

    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "付款人银行")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    private String payerPhone;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    private String remark3;
}
