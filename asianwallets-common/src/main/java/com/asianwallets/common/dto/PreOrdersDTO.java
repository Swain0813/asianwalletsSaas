package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "预授权订单输入实体", description = "预授权订单输入实体")
public class PreOrdersDTO extends BasePageHelper {

    @ApiModelProperty(value = "预授权订单id")
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

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "产品编码")
    private Integer productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "通道流水号")
    private String channelNumber;

    @ApiModelProperty(value = "订单状态")//1-预授权成功 2-预授权失败 3-冲正成功 4-撤销成功  5-预授权完成
    private Byte orderStatus;

    @ApiModelProperty(value = "订单创建开始时间")
    private String startDate;

    @ApiModelProperty(value = "订单创建结束时间")
    private String endDate;

    @ApiModelProperty(value = "预授权开始完成时间")
    private String startPayFinishTime;

    @ApiModelProperty(value = "预授权结束完成时间")
    private String endPayFinishTime;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "产品id")
    private String productId;
}
