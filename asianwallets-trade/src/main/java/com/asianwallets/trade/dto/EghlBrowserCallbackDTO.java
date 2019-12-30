package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/5/29 16:08
 * @Description: AD3回调信息实体
 */
@Data
@ApiModel(value = "eghl回调浏览器输入实体", description = "eghl回调浏览器输入实体")
public class EghlBrowserCallbackDTO {

    @ApiModelProperty(value = "交易类型")
    private String TransactionType;

    @ApiModelProperty(value = "支付方式")
    private String PymtMethod;

    @ApiModelProperty(value = "服务id")
    private String ServiceID;

    @ApiModelProperty(value = "支付id")
    private String PaymentID;

    @ApiModelProperty(value = "订单号")
    private String OrderNumber;

    @ApiModelProperty(value = "订单金额")
    private String Amount;

    @ApiModelProperty(value = "币种code")
    private String CurrencyCode;

    @ApiModelProperty(value = "签名1")
    private String HashValue;

    @ApiModelProperty(value = "签名2")
    private String HashValue2;

    @ApiModelProperty(value = "通道流水号")
    private String TxnID;

    @ApiModelProperty(value = "银行机构号")
    private String IssuingBank;

    @ApiModelProperty(value = "通道状态")
    private String TxnStatus;

    @ApiModelProperty(value = "通道信息")
    private String TxnMessage;

    @ApiModelProperty(value = "authcode")
    private String AuthCode;

}
