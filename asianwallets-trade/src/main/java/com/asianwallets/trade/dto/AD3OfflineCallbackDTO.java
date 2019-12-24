package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AD3线下回调接口输入实体", description = "AD3线下回调接口输入实体")
public class AD3OfflineCallbackDTO {

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "字符集")
    private String inputCharset;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "终端编号")
    private String terminalId;

    @ApiModelProperty(value = "操作员id")
    private String operatorId;

    @ApiModelProperty(value = "商户上送的商户订单号")
    private String merorderNo;

    @ApiModelProperty(value = "时间")
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")
    private String merorderAmount;

    @ApiModelProperty(value = "付款金额")
    private String payAmount;

    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "回调地址")
    private String receiveUrl;

    @ApiModelProperty(value = "描述")
    private String body;

    @ApiModelProperty(value = "备注信息")
    private String ext1;

    @ApiModelProperty(value = "备注信息")
    private String ext2;

    @ApiModelProperty(value = "备注信息")
    private String ext3;

    @ApiModelProperty(value = "系统流水号")
    private String txnId;

    @ApiModelProperty(value = "平台系统处理时间yyyyMMddHHmmss")
    private String txnDate;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "响应码")
    private String respcode;

    @ApiModelProperty(value = "响应信息")
    private String respmsg;

    @ApiModelProperty(value = "签名")
    private String signMsg;
}
