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
@Table(name = "pre_orders")
@ApiModel(value = "预授权订单表", description = "预授权订单表")
public class PreOrders extends BaseEntity {

    @ApiModelProperty(value = "机构编号")
    @Column(name = "institution_id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    @Column(name = "institution_name")
    private String institutionName;

    @ApiModelProperty(value = "产品类型")//1-收款 2-付款
    @Column(name = "trade_type")
    private Byte tradeType;

    @ApiModelProperty(value = "交易类型")//1-线上 2-线下
    @Column(name = "trade_direction")
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户编号")
    @Column(name = "merchant_id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    @Column(name = "merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "商户类型 3-普通商户 4-代理商户 5-集团商户")
    @Column(name = "merchant_type")
    private String merchantType;

    @ApiModelProperty(value = "二级商户编号")
    @Column(name = "second_merchant_code")
    private String secondMerchantCode;

    @ApiModelProperty(value = "二级商户名称")
    @Column(name = "second_merchant_name")
    private String secondMerchantName;

    @ApiModelProperty(value = "集团商户编号")
    @Column(name = "group_merchant_code")
    private String groupMerchantCode;

    @ApiModelProperty(value = "集团商户名称")
    @Column(name = "group_merchant_name")
    private String groupMerchantName;

    @ApiModelProperty(value = "代理商编号")
    @Column(name = "agent_code")
    private String agentCode;

    @ApiModelProperty(value = "代理商名称")
    @Column(name = "agent_name")
    private String agentName;

    @ApiModelProperty(value = "商户订单号")
    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户订单时间")
    @Column(name = "merchant_order_time")
    private Date merchantOrderTime;

    @ApiModelProperty(value = "订单币种")
    @Column(name = "order_currency")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "完成金额")
    @Column(name = "complete_amount")
    private BigDecimal completeAmount;

    @ApiModelProperty(value = "设备编号")
    @Column(name = "imei")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    @Column(name = "operator_id")
    private String operatorId;

    @ApiModelProperty(value = "订单币种转交易币种汇率")
    @Column(name = "order_for_trade_rate")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "交易币种转订单币种汇率")
    @Column(name = "trade_for_order_rate")
    private BigDecimal tradeForOrderRate;

    @ApiModelProperty(value = "换汇汇率")
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇时间")
    @Column(name = "exchange_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")//1-换汇成功 2-换汇失败
    @Column(name = "exchange_status")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "产品编号")
    @Column(name = "product_code")
    private Integer productCode;

    @ApiModelProperty(value = "商品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    @Column(name = "product_description")
    private String productDescription;

    @ApiModelProperty(value = "通道编号")
    @Column(name = "channel_code")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    @Column(name = "channel_name")
    private String channelName;

    @ApiModelProperty(value = "交易币种")
    @Column(name = "trade_currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    @Column(name = "trade_amount")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "持卡人扣款金额")
    @Column(name = "debit_amount")
    private BigDecimal debitAmount;

    @ApiModelProperty(value = "订单状态")//1-预授权成功 2-预授权失败 3-冲正成功 4-撤销成功  5-预授权完成
    @Column(name = "order_status")
    private Byte orderStatus;

    @ApiModelProperty(value = "通道流水号")
    @Column(name = "channel_number")
    private String channelNumber;

    @ApiModelProperty(value = "支付方式")
    @Column(name = "pay_method")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    @Column(name = "req_ip")
    private String reqIp;

    @ApiModelProperty(value = "预授权请求通道时间")
    @Column(name = "report_channel_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "预授权完成请求通道时间")
    @Column(name = "channel_callback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "付款人名称")
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

    @ApiModelProperty(value = "付款人电话")
    @Column(name = "payer_phone")
    private String payerPhone;

    @ApiModelProperty(value = "付款人地址")
    @Column(name = "payer_address")
    private String payerAddress;

    @ApiModelProperty(value = "银行机构号")
    @Column(name = "issuer_id")
    private String issuerId;

    @ApiModelProperty(value = "银行名称")
    @Column(name = "bank_name")
    private String bankName;

    @ApiModelProperty(value = "浏览器回调地址")
    @Column(name = "browser_url")
    private String browserUrl;

    @ApiModelProperty(value = "服务器回调地址")
    @Column(name = "server_url")
    private String serverUrl;

    @ApiModelProperty(value = "语言")
    @Column(name = "language")
    private String language;

    @ApiModelProperty(value = "银行卡号")
    @Column(name = "user_bank_card_no")
    private String userBankCardNo;

    @ApiModelProperty(value = "CVV2")
    @Column(name = "cvv2")
    private String cvv2;

    @ApiModelProperty(value = "卡有效期")
    @Column(name = "valid")
    private String valid;

    @ApiModelProperty(value = "磁道信息")
    @Column(name = "track_data")
    private String trackData;

    @ApiModelProperty(value = "remark1")
    @Column(name = "remark1")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    @Column(name = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    @Column(name = "remark3")
    private String remark3;

    @ApiModelProperty(value = "remark4")
    @Column(name = "remark4")
    private String remark4;

    @ApiModelProperty(value = "remark5")
    @Column(name = "remark5")
    private String remark5;

    @ApiModelProperty(value = "remark6")
    @Column(name = "remark6")
    private String remark6;

    @ApiModelProperty(value = "remark7")
    @Column(name = "remark7")
    private String remark7;

    @ApiModelProperty(value = "remark8")
    @Column(name = "remark8")
    private String remark8;
}