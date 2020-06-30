package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "线下交易输入实体", description = "线下交易输入实体")
public class OfflineTradeDTO {

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotBlank(message = "50002")
    @ApiModelProperty(value = "商户订单号")
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

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

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

    @ApiModelProperty(value = "银行卡号")
    private String userBankCardNo;

    @ApiModelProperty(value = "cvv2")
    private String cvv2;

    @ApiModelProperty(value = "卡有效期")
    private String valid;

    @ApiModelProperty(value = "磁道信息")
    private String trackData;

    @ApiModelProperty(value = "银行卡pin")
    private String pin;

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

    @ApiModelProperty(value = "签购单")
    private String signOrder;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    private String remark3;

    @ApiModelProperty(value = "聚合码")
    private String merchantCardCode;


    public OfflineTradeDTO(OfflineCodeTradeDTO offlineCodeTradeDTO) {
        this.merchantId = offlineCodeTradeDTO.getMerchantId();
        this.orderNo = offlineCodeTradeDTO.getOrderNo();
        //币种为产品币种
//        this.orderCurrency = offlineCodeTradeDTO.getOrderCurrency();
        this.orderAmount = offlineCodeTradeDTO.getOrderAmount();
        this.orderTime = offlineCodeTradeDTO.getOrderTime();
        this.merchantCardCode = offlineCodeTradeDTO.getMerchantCardCode();
    }

    public OfflineTradeDTO() {
    }
}
