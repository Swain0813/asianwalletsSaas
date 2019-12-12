package com.asianwallets.common.dto.help2pay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderPayment;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description: help2Pay通道请求汇款实体
 * @author: YangXu
 * @create: 2019-07-17 10:03
 **/
@Data
@ApiModel(value = "help2Pay通道请求汇款实体", description = "help2Pay通道请求汇款实体")
public class Help2PayOutDTO {

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "ClientIP")
    private String ClientIP;

    @ApiModelProperty(value = "ReturnURI")
    private String ReturnURI;

    @ApiModelProperty(value = "机构商户号")
    private String MerchantCode;

    @ApiModelProperty(value = "流水号")
    private String TransactionID;

    @ApiModelProperty(value = "币种")
    private String CurrencyCode;

    @ApiModelProperty(value = "机构号")
    private String MemberCode;

    @ApiModelProperty(value = "金额")
    private String Amount;

    @ApiModelProperty(value = "时间")
    private String TransactionDateTime;

    @ApiModelProperty(value = "银行编码")
    private String BankCode;

    @ApiModelProperty(value = "银行名称")
    private String toBankAccountName;

    @ApiModelProperty(value = "卡号")
    private String toBankAccountNumber;

    /***************************** 非必填 *************************************/
    @ApiModelProperty(value = "商户号")
    private String toProvince;

    @ApiModelProperty(value = "商户号")
    private String toCity;

    /********** md5key? **********/
    @ApiModelProperty(value = "商户号")
    private String SecurityCode;

    @ApiModelProperty(value = "Channel")
    private Channel channel;


    public Help2PayOutDTO() {
    }

    public Help2PayOutDTO(OrderPayment orderPayment, Channel channel) {
        //this.key = key;
        //this.ClientIP = "47.100.197.214";
        this.ReturnURI = "https://pag.asianwallets.com/tra/payOutCallBack/help2PayCallBack";
        this.MerchantCode = channel.getChannelMerchantId();
        this.TransactionID = orderPayment.getId();
        this.CurrencyCode = orderPayment.getPaymentCurrency();
        this.MemberCode = orderPayment.getInstitutionCode();
        this.Amount = orderPayment.getPaymentAmount().toString();
        this.TransactionDateTime = DateToolUtils.LONG_DATE_FORMAT_AA.format(new Date());
        this.BankCode = orderPayment.getBankCode();
        this.toBankAccountName = orderPayment.getBankAccountName();
        this.toBankAccountNumber = orderPayment.getBankAccountNumber();
        this.toProvince = "";
        this.toCity = "";
        this.SecurityCode = channel.getMd5KeyStr();
        this.channel = channel;
    }
}
