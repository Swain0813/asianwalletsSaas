package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shenxinran
 * @Date: 2019/3/15 14:08
 * @Description: AD3回调信息实体
 */
@Data
@ApiModel(value = "AD3回调信息实体", description = "AD3回调信息实体")
public class AD3OnlineCallbackDTO {

    @ApiModelProperty(value = "固定值1.1.0")
    private String version;

    @ApiModelProperty(value = "字符集")
    private String inputCharset;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "时间")
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")
    private String merorderAmount;

    @ApiModelProperty(value = "付款金额")
    private String payAmount;

    @ApiModelProperty(value = "付款币种")
    private String payCurrency;

    @ApiModelProperty(value = "付款时的汇率")
    private String payExchangeRate;

    @ApiModelProperty(value = "结算金额")
    private String merSTOrderAmount;

    @ApiModelProperty(value = "结算币种")
    private String merSTCurrency;

    @ApiModelProperty(value = "结算时的汇率")
    private String exchangeRate;

    @ApiModelProperty(value = "备注信息")
    private String ext1;


    @ApiModelProperty(value = "备注信息")
    private String ext2;

    @ApiModelProperty(value = "备注信息")
    private String ext3;

    @ApiModelProperty(value = "ALLDEBIT的系统流水号")
    private String txnid;

    @ApiModelProperty(value = "ALLDEBIT的系统处理时间yyyyMMddHHmmss")
    private String txndate;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "交易手续费")
    private String tradeFee;

    @ApiModelProperty(value = "交易手续费币种")
    private String tradeFeeCurrency;

    @ApiModelProperty(value = "响应码")
    private String respcode;

    @ApiModelProperty(value = "响应信息")
    private String respmsg;

    @ApiModelProperty(value = "商户验签")
    private String signMsg;


}
