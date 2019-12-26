package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "pos查询订单信息输出实体", description = "pos查询订单信息输出实体")
public class PosQueryOrderListVO {

    @ApiModelProperty(value = "系统订单流水号")
    private String referenceNo;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "机构名称")
    private String merchantName;

    @ApiModelProperty(value = "机构订单号")
    private String orderNo;

    @ApiModelProperty(value = "机构订单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderTime;

    @ApiModelProperty(value = "数据库的订单金额")
    @JsonIgnore
    private BigDecimal amount;

    @ApiModelProperty(value = "订单金额")
    private String orderAmount;

    @ApiModelProperty(value = "设备编号")
    private String imei;

    @ApiModelProperty(value = "操作员ID")
    private String operatorId;

    @ApiModelProperty(value = "产品名称")
    private String payType;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;

    @ApiModelProperty(value = "交易状态")//(1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期)
    private Byte txnstatus;

    @ApiModelProperty(value = "退款状态")//退款状态(1-退款中 2-部分退款成功 3-退款成功 4-退款失败)
    private Byte refundStatus;

    @ApiModelProperty(value = "撤销状态")//(1-撤销中 2-撤销成功 3-撤销失败)
    private Byte reverseStatus;

    @ApiModelProperty(value = "支付方式图片1 带字的(支付方式表)")//查询订单详情时 使用的是channel表里的channel_img
    private String payTypeImgOne;

    @ApiModelProperty(value = "支付方式图片2 圆的(字典表)")
    private String payTypeImgTwo;

    @ApiModelProperty(value = "支付方式名称")
    private String payTypeName;

    @ApiModelProperty(value = "币种默认值")
    private String defaultValue;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;
}
