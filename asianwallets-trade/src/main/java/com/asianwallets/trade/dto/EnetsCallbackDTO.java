package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/5/29 16:08
 * @Description: enets回调浏览器输入实体
 */
@Data
@ApiModel(value = "enets回调浏览器输入实体", description = "enets回调浏览器输入实体")
public class EnetsCallbackDTO {

    @ApiModelProperty(value = "通道商户号 channelMerchantId")
    private String netsMid;

    @ApiModelProperty(value = "订单号")
    private String merchantTxnRef;

    @ApiModelProperty(value = "商户订单时间")
    private String merchantTxnDtm;

    @ApiModelProperty(value = "")
    private String b2sTxnEndURL;

    @ApiModelProperty(value = "交易状态")
    private String netsTxnStatus;

    @ApiModelProperty(value = "msg")
    private String netsTxnMsg;

    @ApiModelProperty(value = "enets通道流水号")
    private String netsTxnRef;

    @ApiModelProperty(value = "")
    private String bankId;

    @ApiModelProperty(value = "")
    private String bankRefCode;

    @ApiModelProperty(value = "")
    private String currencyCode;

    @ApiModelProperty(value = "")
    private String paymentType;

    @ApiModelProperty(value = "code")
    private String stageRespCode;

    @ApiModelProperty(value = "")
    private String actionCode;

    @ApiModelProperty(value = "")
    private String txnRand;

    @ApiModelProperty(value = "")
    private String netsAmountDeducted;

    @ApiModelProperty(value = "签名")
    private String hmac;

}
