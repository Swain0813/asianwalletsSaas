package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "订单详情输出实体", description = "订单详情输出实体")
public class OrdersDetailVO {

    //----------------------【订单信息】----------------------------
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "订单流水号")
    private String orderId;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;

    @ApiModelProperty(value = "产品名称")
    private String payType;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @ApiModelProperty(value = "订单状态")
    private Byte tradeStatus;

    @ApiModelProperty(value = "订单备注")
    private String ordersRemark;


    //----------------------【换汇信息】----------------------------
    @ApiModelProperty(value = "换汇时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "订单转交易币种汇率")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "换汇汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇状态")
    private Byte exchangeStatus;

    @ApiModelProperty(value = "换汇备注(remark4)")
    private String exchangeRateRemark;

    //----------------------【通道信息】----------------------------
    @ApiModelProperty(value = "上报通道时间(请求扣款时间)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "付款人名称")
    private String payerName;

    @ApiModelProperty(value = "付款人账户")
    private String payerAccount;

    @ApiModelProperty(value = "付款人银行")
    private String payerBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String payerEmail;

    @ApiModelProperty(value = "通道回调时间(扣款完成时间)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "通道手续费")
    private BigDecimal channelFee;

    @ApiModelProperty(value = "通道备注(remark5)")
    private String channelRemark;


    //----------------------【退款信息】----------------------------
//    @ApiModelProperty(value = "退款订单创建时间")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date orderRefundCreateTime;
//
//    @ApiModelProperty(value = "退款流水号")
//    private String orderRefundId;
//
//    @ApiModelProperty(value = "退款金额")
//    private BigDecimal orderRefundAmount;
//
//    @ApiModelProperty(value = "退款汇率")
//    private BigDecimal refundExchangeRate;
//
//    @ApiModelProperty(value = "通道退款金额")
//    private BigDecimal refundChannelAmount;
//
//    @ApiModelProperty(value = "退款交易币种")
//    private String refundTradeCurrency;
//
//    @ApiModelProperty(value = "退款状态")
//    private Byte refundStatus;
//
//    @ApiModelProperty(value = "退款完成时间")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date refundFinishTime;
//
//    @ApiModelProperty(value = "退款手续费")
//    private BigDecimal refundFee;
//
//    @ApiModelProperty(value = "退款备注")
//    private String refundRemark;


    //----------------------【物流信息】----------------------------
    @ApiModelProperty(value = "发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "发货单号")
    private String invoiceNo;

    @ApiModelProperty(value = "服务商名称")
    private String providerName;

    @ApiModelProperty(value = "发货状态")
    private Byte deliveryStatus;

    @ApiModelProperty(value = "物流备注(remark6)")
    private String logisticsRemark;

    //----------------------【退款信息】----------------------------
    public List<OrdersDetailRefundVO> ordersDetailRefundVOS;
}
