package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "商户交易对账单中文输出实体", description = "商户交易对账单中文输出实体")
public class ExportTradeCheckAccountDetailVO {

    @ApiModelProperty("订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty("商户编号")
    private String merchantId;

    @ApiModelProperty("商户名称")
    private String merchantName;

    @ApiModelProperty("订单流水号")
    private String orderId;

    @ApiModelProperty("商户订单号")
    private String merchantOrderId;

    @ApiModelProperty("交易金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("交易币种")
    private String orderCurrency;

    @ApiModelProperty("交易类型")
    private String tradeTypeName;

    @ApiModelProperty("交易状态")
    private String tradeStatusName;

    @ApiModelProperty("交易方向")
    private Byte tradeDirection;

    @ApiModelProperty("手续费类型")
    private String rateType;

    @ApiModelProperty("费率")
    private BigDecimal rate;

    @ApiModelProperty(value = "保底手续费")
    private BigDecimal minTate = BigDecimal.ZERO;

    @ApiModelProperty(value = "封顶手续费")
    private BigDecimal maxTate = BigDecimal.ZERO;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("交易完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date channelCallbackTime;
}