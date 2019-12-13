package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "OrdersRefundDetailVO", description = "退款订单详情输出实体")
public class OrdersRefundDetailVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "商户订单号")
    private String institutionOrderId;

    @ApiModelProperty(value = "订单流水号")
    private String id;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "通道手续费")
    private BigDecimal channelFee;

    @ApiModelProperty(value = "附加值")
    private BigDecimal addValue;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "设备编号")//收款设备编号(线下订单)
    private String deviceCode;

    @ApiModelProperty(value = "设备操作员")//收银员编号(线下订单)
    private String deviceOperator;

    @ApiModelProperty(value = "订单状态")
    private Byte tradeStatus;


    @ApiModelProperty(value = "换汇时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "换汇汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "换汇状态")
    private Byte exchangeStatus;


    @ApiModelProperty(value = "上报通道时间(请求扣款时间)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportChannelTime;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "银行机构号")
    private String issuerId;

    @ApiModelProperty(value = "付款人名称")
    private String draweeName;

    @ApiModelProperty(value = "付款人账户")
    private String draweeAccount;

    @ApiModelProperty(value = "付款人银行")
    private String draweeBank;

    @ApiModelProperty(value = "付款人邮箱")
    private String draweeEmail;

    @ApiModelProperty(value = "商品名称")
    private String productName;


    @ApiModelProperty(value = "通道回调时间(扣款完成时间)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "备注")//订单失败原因
    private String remark;

    @ApiModelProperty(value = "字典支付方式名称")
    private String dName;

    @ApiModelProperty(value = "产品名称")
    private String payName;

    @ApiModelProperty(value = "备注1")
    private String remark1;

    @ApiModelProperty(value = "备注2")
    private String remark2;

    @ApiModelProperty(value = "备注3")
    private String remark3;

    @ApiModelProperty(value = "备注4")
    private String remark4;

    @ApiModelProperty(value = "手续费付款方")
    private Byte feePayer;

    @ApiModelProperty(value = "手费率类型")
    private String rateType;

    @ApiModelProperty(value = "银行名")
    private String bankName;

}
