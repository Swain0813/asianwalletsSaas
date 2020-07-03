package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "预授权订单详情输出参数", description = "预授权订单详情输出参数")
public class PreOrdersVO {

    @ApiModelProperty(value = "预授权订单号")
    public String id;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "产品类型")//1-收款 2-付款
    private Byte tradeType;

    @ApiModelProperty(value = "交易类型")//1-线上 2-线下
    private Byte tradeDirection;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户类型 3-普通商户 4-代理商户 5-集团商户")
    private String merchantType;

    @ApiModelProperty(value = "二级商户编号")
    private String secondMerchantCode;

    @ApiModelProperty(value = "二级商户名称")
    private String secondMerchantName;

    @ApiModelProperty(value = "集团商户编号")
    private String groupMerchantCode;

    @ApiModelProperty(value = "集团商户名称")
    private String groupMerchantName;

    @ApiModelProperty(value = "代理商编号")
    private String agentCode;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户订单时间")
    private Date merchantOrderTime;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "完成金额")
    private BigDecimal completeAmount;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @ApiModelProperty(value = "订单币种转交易币种汇率")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "交易币种转订单币种汇率")
    private BigDecimal tradeForOrderRate;

    @ApiModelProperty(value = "换汇汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "换汇状态")//1-换汇成功 2-换汇失败
    private Byte exchangeStatus;

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    private String productDescription;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "持卡人扣款金额")
    private BigDecimal debitAmount;

    @ApiModelProperty(value = "冲正或撤销请求时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;

    @ApiModelProperty(value = "订单状态")//0-预授权中 1-预授权成功 2-预授权失败 3-冲正成功 4-撤销成功  5-预授权完成
    private Byte orderStatus;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "支付方式")
    private String payMethod;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "预授权请求通道时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "预授权完成请求通道时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "付款人名称")
    private String payerName;

    @ApiModelProperty(value = "付款人账户")
    private String payerAccount;

    @ApiModelProperty(value = "付款人银行")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "付款人电话")
    private String payerPhone;

    @ApiModelProperty(value = "付款人地址")
    private String payerAddress;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "浏览器回调地址")
    private String browserUrl;

    @ApiModelProperty(value = "服务器回调地址")
    private String serverUrl;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "银行卡号")
    private String userBankCardNo;

    @ApiModelProperty(value = "CVV2")
    private String cvv2;

    @ApiModelProperty(value = "卡有效期")
    private String valid;

    @ApiModelProperty(value = "磁道信息")
    private String trackData;

    @ApiModelProperty(value = "pin")
    private String pin;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "批次号+流水号+日期")
    private String remark1;

    @ApiModelProperty(value = "remark2")
    private String remark2;

    @ApiModelProperty(value = "remark3")
    private String remark3;

    @ApiModelProperty(value = "remark4")
    private String remark4;

    @ApiModelProperty(value = "remark5")
    private String remark5;

    @ApiModelProperty(value = "remark6")
    private String remark6;

    @ApiModelProperty(value = "remark7")
    private String remark7;

    @ApiModelProperty(value = "remark8")
    private String remark8;

    //----------------------【订单信息】----------------------------

    @ApiModelProperty(value = "订单流水号")
    private String orderId;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;

    @ApiModelProperty(value = "订单状态")
    private Byte tradeStatus;
}
