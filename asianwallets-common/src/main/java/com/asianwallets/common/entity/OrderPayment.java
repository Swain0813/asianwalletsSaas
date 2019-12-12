package com.asianwallets.common.entity;

import com.asianwallets.common.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "order_payment")
@ApiModel(value = "付款订单表", description = "付款订单表")
public class OrderPayment extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_code")
    private String institutionCode;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "机构上报时间")
    @Column(name = "institution_order_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date institutionOrderTime;

    @ApiModelProperty(value = "机构上报付款流水号")
    @Column(name = "institution_order_id")
    private String institutionOrderId;

    @ApiModelProperty(value = "机构上报付款批次号")
    @Column(name = "institution_batch_no")
    private String institutionBatchNo;

    @ApiModelProperty(value = "通道付款批次号")
    @Column(name = "channel_batch_no")
    private String channelBatchNo;

    @ApiModelProperty(value = "系统付款批次号")
    @Column(name = "system_batch_no")
    private String systemBatchNo;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "汇款币种")
    @Column(name = "payment_currency")
    private String paymentCurrency;

    @ApiModelProperty(value = "汇款金额")
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    @ApiModelProperty(value = "汇款银行名称")
    @Column(name = "bank_account_name")
    private String bankAccountName;

    @ApiModelProperty(value = "汇款银行卡号")
    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @ApiModelProperty(value = "汇款国家")
    @Column(name = "receiver_country")
    private String receiverCountry;

    @ApiModelProperty(value = "汇款地址")
    @Column(name = "receiver_adress")
    private String receiverAdress;

    @ApiModelProperty(value = "银行code")
    @Column(name = "bank_code")
    private String bankCode;

    @ApiModelProperty(value = "swiftCode")
    @Column(name = "swift_code")
    private String swiftCode;

    @ApiModelProperty(value = "旧汇率(订单转交易)")
    @Column(name = "old_exchange_rate")
    private BigDecimal oldExchangeRate;

    @ApiModelProperty(value = "汇率")
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇时间")
    @Column(name = "exchange_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态:1-换汇成功 2-换汇失败")
    @Column(name = "exchange_status")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "产品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "汇款状态:1-待汇款 2-汇款中 3-汇款成功 4-汇款失败")
    @Column(name = "payout_status")
    private Byte payoutStatus;

    @ApiModelProperty(value = "费率类型 (dic_7_1-单笔费率,dic_7_2-单笔定额)")
    @Column(name = "rate_type")
    private String rateType;

    @ApiModelProperty(value = "费率 根据订单币种")
    @Column(name = "rate")
    private BigDecimal rate;

    @ApiModelProperty(value = "手续费")
    @Column(name = "fee")
    private BigDecimal fee;

    @ApiModelProperty(value = "手续费付款方 1:内扣 2:外扣")
    @Column(name = "fee_payer")
    private Byte feePayer;

    @ApiModelProperty(value = "计费状态：1-计费成功，2-计费失败")
    @Column(name = "charge_status")
    private Byte chargeStatus;

    @ApiModelProperty(value = "计费时间")
    @Column(name = "charge_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date chargeTime;

    @ApiModelProperty(value = "付款方式即产品表中的支付方式")
    @Column(name = "pay_method")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "上报通道时间即付款请求时间")
    @Column(name = "report_channel_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "通道回调时间即付款完成时间")
    @Column(name = "channel_callback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "浮动率 必填")
    @Column(name = "float_rate")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "附加值 必填")
    @Column(name = "add_value")
    private BigDecimal addValue;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "server_url")
    private String serverUrl;

    @ApiModelProperty(value = "浏览器返回地址")
    @Column(name = "browser_url")
    private String browserUrl;

    @ApiModelProperty(value = "md5key")
    @Column(name = "md5key")
    private String md5key;

    @ApiModelProperty(value = "签名")
    @Column(name = "sign")
    private String sign;

    @ApiModelProperty(value = "应结算时间")
    @Column(name = "extend1")
    private String extend1;

    @ApiModelProperty(value = "开户人名称")
    @Column(name = "extend2")
    private String extend2;

    @ApiModelProperty(value = "通道服务名")
    @Column(name = "extend3")
    private String extend3;

    @ApiModelProperty(value = "人工汇款标记")//0-系统汇款 1-人工汇款
    @Column(name = "extend4")
    private boolean extend4;

    @ApiModelProperty(value = "代理商户号")
    @Column(name = "extend5")
    private String extend5;

    @ApiModelProperty(value = "旧汇率(交易转订单)")
    @Column(name = "extend6")
    private String extend6;

    @ApiModelProperty(value = "extend7")
    @Column(name = "extend7")
    private String extend7;

    @ApiModelProperty(value = "extend8")
    @Column(name = "extend8")
    private String extend8;

    @ApiModelProperty(value = "extend9")
    @Column(name = "extend9")
    private String extend9;


}
