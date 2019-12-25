package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ApiModel(value = "DCC报表查询输出实体", description = "DCC报表查询输出实体")
public class DccReportVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户订单号")
    private String merchantOrderId;

    @ApiModelProperty(value = "交易流水号")
    private String id;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "原始汇率")
    private BigDecimal orderForTradeRate;

    @ApiModelProperty(value = "换汇汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal floatRate;

    @ApiModelProperty(value = "浮动金额")
    private BigDecimal floatAmount;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "支付完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;

    @ApiModelProperty(value = "交易状态")
    private Byte tradeStatus;
}