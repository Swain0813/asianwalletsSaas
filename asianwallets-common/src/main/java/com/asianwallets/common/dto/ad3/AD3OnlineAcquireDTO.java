package com.asianwallets.common.dto.ad3;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.constant.AD3Constant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author shenxinran
 * @Date: 2019/3/12 11:38
 * @Description: AD3线上收单接口参数实体
 */
@Data
@ApiModel(value = "AD3线上收单接口参数实体", description = "AD3线上收单接口参数实体")
public class AD3OnlineAcquireDTO {

    @ApiModelProperty(value = "版本")
    private String version = "v1.0";

    @ApiModelProperty(value = "字符集")
    private String inputCharset;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "开放给商户的唯一编号")
    private String merchantId;

    @ApiModelProperty(value = "商户上送的商户订单号")
    private String merOrderNo;

    @ApiModelProperty(value = "订单时间, 日期格式：yyyyMMddHHmmss")
    private String merorderDatetime;

    @ApiModelProperty(value = "订单币种")
    private String merorderCurrency;

    @ApiModelProperty(value = "订单金额")
    private String merorderAmount;

    @ApiModelProperty(value = "浏览器返回地址")
    private String pickupUrl;

    @ApiModelProperty(value = "交易结果后台通知地址")
    private String receiveUrl;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "银行机构代码")
    private String issuerId;

    @ApiModelProperty(value = "备注信息1")
    private String ext1;

    @ApiModelProperty(value = "备注信息2")
    private String ext2;

    @ApiModelProperty(value = "备注信息3")
    private String ext3;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "付款人姓名")
    private String payerName;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    private String payerTelephone;

    @ApiModelProperty(value = "商品ID")
    private String productId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品数量")
    private String productNum;

    @ApiModelProperty(value = "商品单价")
    private String productPrice;

    @ApiModelProperty(value = "商品标价币种")
    private String productCurrency;

    @ApiModelProperty(value = "商户加密证书方式")//1为使用平台提供的密钥 2为使用自己生成的密钥
    private String merchantSignType;

    @ApiModelProperty(value = "商户签名")
    private String signMsg;

    @ApiModelProperty(value = "下单地址")
    private String url;

    public AD3OnlineAcquireDTO() {

    }

    public AD3OnlineAcquireDTO(Orders orders, Channel channel, String receiveUrl, String pickupUrl) {
        this.inputCharset = AD3Constant.CHARSET_UTF_8;
        this.language = AD3Constant.LANGUAGE_CN;//语言
        this.merchantId = channel.getChannelMerchantId();
        this.merOrderNo = orders.getId();
        this.merorderDatetime = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        this.merorderCurrency = orders.getTradeCurrency();
        this.merorderAmount = String.valueOf(orders.getChannelAmount());
        this.payType = channel.getPayCode();
        this.issuerId = channel.getIssuerId();
        this.receiveUrl = receiveUrl;
        this.pickupUrl = pickupUrl;
        this.ext1 = orders.getRemark1();
        this.ext2 = orders.getRemark2();
        this.ext3 = orders.getRemark3();
        this.businessType = AD3Constant.BUSINESS_OUT;
        this.payerName = orders.getPayerName();
        this.payerEmail = orders.getPayerEmail();
        this.payerTelephone = orders.getPayerPhone();
        this.productId = "";
        this.productName = orders.getProductName();
        this.productNum = "";
        this.productPrice = "";
        this.productCurrency = "";
        //1为使用平台提供的密钥 2为使用自己生成的密钥 channel.getExtend1()
        this.merchantSignType = "2";
    }

    @Override
    public String toString() {
        return "" +
                "version:" + (StringUtils.isEmpty(version) ? "" : version) + '\n' +
                "inputCharset:" + (StringUtils.isEmpty(inputCharset) ? "" : inputCharset) + '\n' +
                "language:" + (StringUtils.isEmpty(language) ? "" : language) + '\n' +
                "merchantId:" + (StringUtils.isEmpty(merchantId) ? "" : merchantId) + '\n' +
                "merOrderNo:" + (StringUtils.isEmpty(merOrderNo) ? "" : merOrderNo) + '\n' +
                "merorderDatetime:" + (StringUtils.isEmpty(merorderDatetime) ? "" : merorderDatetime) + '\n' +
                "merorderCurrency:" + (StringUtils.isEmpty(merorderCurrency) ? "" : merorderCurrency) + '\n' +
                "merorderAmount:" + (StringUtils.isEmpty(merorderAmount) ? "" : merorderAmount) + '\n' +
                "pickupUrl:" + (StringUtils.isEmpty(pickupUrl) ? "" : pickupUrl) + '\n' +
                "receiveUrl:" + (StringUtils.isEmpty(receiveUrl) ? "" : receiveUrl) + '\n' +
                "payType:" + (StringUtils.isEmpty(payType) ? "" : payType) + '\n' +
                "issuerId:" + (StringUtils.isEmpty(issuerId) ? "" : issuerId) + '\n' +
                "ext1:" + (StringUtils.isEmpty(ext1) ? "" : ext1) + '\n' +
                "ext2:" + (StringUtils.isEmpty(ext2) ? "" : ext2) + '\n' +
                "ext3:" + (StringUtils.isEmpty(ext3) ? "" : ext3) + '\n' +
                "businessType:" + (StringUtils.isEmpty(businessType) ? "" : businessType) + '\n' +
                "payerName:" + (StringUtils.isEmpty(payerName) ? "" : payerName) + '\n' +
                "payerEmail:" + (StringUtils.isEmpty(payerEmail) ? "" : payerEmail) + '\n' +
                "payerTelephone:" + (StringUtils.isEmpty(payerTelephone) ? "" : payerTelephone) + '\n' +
                "productId:" + (StringUtils.isEmpty(productId) ? "" : productId) + '\n' +
                "productName:" + (StringUtils.isEmpty(productName) ? "" : productName) + '\n' +
                "productNum:" + (StringUtils.isEmpty(productNum) ? "" : productNum) + '\n' +
                "productPrice:" + (StringUtils.isEmpty(productPrice) ? "" : productPrice) + '\n' +
                "productCurrency:" + (StringUtils.isEmpty(productCurrency) ? "" : productCurrency) + '\n' +
                "merchantSignType:" + (StringUtils.isEmpty(merchantSignType) ? "" : merchantSignType) + '\n' +
                "signMsg:" + (StringUtils.isEmpty(signMsg) ? "" : signMsg) + '\n' +
                "url:" + (StringUtils.isEmpty(url) ? "" : url) + '\n';
    }
}