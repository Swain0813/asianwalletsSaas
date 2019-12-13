package com.asianwallets.common.dto.eghl;

import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.Sha256Tools;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: XuWenQi
 * @create: 2019-05-28 14:40
 **/

@Data
@ApiModel(value = "EGHL通道请求实体", description = "EGHL通道请求实体")
public class EGHLRequestDTO {

    @ApiModelProperty(value = "交易类型")
    private String TransactionType;

    @ApiModelProperty(value = "支付方式")
    private String PymtMethod;

    @ApiModelProperty(value = "通道商户id")
    private String ServiceID;

    @ApiModelProperty(value = "系统流水号")
    private String PaymentID;

    @ApiModelProperty(value = "商户订单号")
    private String OrderNumber;

    @ApiModelProperty(value = "支付通道描述")
    private String PaymentDesc;

    @ApiModelProperty(value = "商户名称")
    private String MerchantName;

    @ApiModelProperty(value = "页面通知地址")
    private String MerchantReturnURL;

    @ApiModelProperty(value = "金额")
    private String Amount;

    @ApiModelProperty(value = "币种code")
    private String CurrencyCode;

    @ApiModelProperty(value = "客户IP")
    private String CustIP;

    @ApiModelProperty(value = "客户名称")
    private String CustName;

    @ApiModelProperty(value = "客户邮箱")
    private String CustEmail;

    @ApiModelProperty(value = "客户手机")
    private String CustPhone;

    @ApiModelProperty(value = "签名")
    private String HashValue;

    @ApiModelProperty(value = "服务器返回地址")
    private String MerchantCallBackURL;

    @ApiModelProperty(value = "语言")
    private String LanguageCode;

    @ApiModelProperty(value = "失效时间")
    private String PageTimeOut;

    @ApiModelProperty(value = "银行机构号")
    private String IssuingBank;

    @ApiModelProperty(value = "md5_key_str")
    private String md5KeyStr;

    @ApiModelProperty(value = "Channel")
    private Channel channel;


    public EGHLRequestDTO() {
    }

    public EGHLRequestDTO(Orders orders, Channel channel, String merchantReturnURL, String merchantCallBackURL) {
        this.TransactionType = "SALE";
        this.PymtMethod = "DD";
        this.ServiceID = channel.getChannelMerchantId();
        this.PaymentID = orders.getId();
        this.OrderNumber = orders.getId();
        this.PaymentDesc = "eGHL";
        this.MerchantName = "";
        this.MerchantReturnURL = merchantReturnURL;//服务回调
        this.MerchantCallBackURL = merchantCallBackURL;//浏览器回调
        this.Amount = String.valueOf(orders.getTradeAmount());
        this.CurrencyCode = orders.getTradeCurrency();
        this.CustIP = orders.getReqIp();
        this.CustName = orders.getPayerName();
        this.CustEmail = orders.getPayerEmail();
        //this.CustPhone = orders.getDraweePhone();
        this.CustPhone = "18688889999";
        this.PageTimeOut = "780";
        this.md5KeyStr = channel.getMd5KeyStr();
        if (TradeConstant.ZH_CN.equals(orders.getLanguage())) {
            this.LanguageCode = "cn";
        } else {
            this.LanguageCode = "en";
        }
        this.IssuingBank = orders.getIssuerId();
        String flag = channel.getMd5KeyStr() + this.getServiceID() + this.getPaymentID() + this.getMerchantReturnURL() + this.getMerchantCallBackURL() + this.getAmount()
                + this.getCurrencyCode() + this.getCustIP() + this.getPageTimeOut();
        this.HashValue = Sha256Tools.encrypt(flag);
        this.channel = channel;
    }
}
