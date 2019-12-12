package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "channels_order")
@ApiModel(value = "通道配置", description = "通道配置")
public class ChannelsOrder extends BaseEntity {

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @ApiModelProperty(value = "渠道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "订单类型")
    @Column(name = "order_type")
    private Byte orderType;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "付款人")
    @Column(name = "payer_name")
    private String payerName;

    @ApiModelProperty(value = "付款人账户")
    @Column(name = "payer_account")
    private String payerAccount;

    @ApiModelProperty(value = "付款人银行")
    @Column(name = "payer_bank")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    @Column(name = "payer_email")
    private String payerEmail;

    @ApiModelProperty(value = "浏览器通知地址")
    @Column(name = "browser_url")
    private String browserUrl;

    @ApiModelProperty(value = "服务器通知地址")
    @Column(name = "server_url")
    private String serverUrl;

    @ApiModelProperty(value = "付款人电话")
    @Column(name = "drawee_phone")
    private String draweePhone;

    //交易状态: 1-付款中  2-付款成功  3-付款失败
    @ApiModelProperty(value = "交易状态")
    @Column(name = "trade_status")
    private Byte tradeStatus;

    @ApiModelProperty(value = "issuerId")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "md5_key_str")
    @Column(name = "md5_key_str")
    private String md5KeyStr;

    @ApiModelProperty(value = "remark1")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    @Column(name = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    @Column(name = "remark3")
    private String remark3;

}