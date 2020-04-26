package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "订单输入实体", description = "订单输入实体")
public class OrdersDTO extends BasePageHelper {

    @ApiModelProperty(value = "订单id")
    private String id;

    @ApiModelProperty(value = "产品类型 1-收款 2-付款")
    private Byte tradeType;

    @ApiModelProperty(value = "交易类型 1-线上 2-线下")
    private Byte tradeDirection;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "商户类型 3-普通商户 4-代理商户 5-集团商户")
    private String merchantType;

    @ApiModelProperty(value = "集团商户编号")
    private String groupMerchantCode;

    @ApiModelProperty(value = "集团商户名称")
    private String groupMerchantName;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "产品编码")
    private Integer productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "交易状态")//交易状态:1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期
    private Byte tradeStatus;

    @ApiModelProperty(value = "撤销状态")//撤销状态：1-撤销中 2-撤销成功 3-撤销失败
    private Byte cancelStatus;

    @ApiModelProperty(value = "退款状态")//退款状态：1-退款中 2-部分退款成功 3-退款成功 4-退款失败
    private Byte refundStatus;

    @ApiModelProperty(value = "交易开始时间")
    private String startDate;

    @ApiModelProperty(value = "交易结束时间")
    private String endDate;

    @ApiModelProperty(value = "支付开始完成时间")
    private String startPayFinishTime;

    @ApiModelProperty(value = "支付结束完成时间")
    private String endPayFinishTime;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "发货状态 1-未发货 2-已发货")
    private Byte deliveryStatus;
}
