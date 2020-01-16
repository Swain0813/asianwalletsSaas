package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易对账详细表输出实体", description = "交易对账详细表输出实体")
public class ExportTradeCheckAccountDetailVO {

    @ApiModelProperty("订单流水号")
    private String orderId;

    @ApiModelProperty("机构编号")
    private String institutionCode;

    @ApiModelProperty("订单创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderCreateTime;

    @ApiModelProperty("设备编号")
    private String deviceCode;

    @ApiModelProperty("机构订单号")
    private String institutionOrderId;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("订单币种")
    private String orderCurrency;

    @ApiModelProperty("订单金额")
    private BigDecimal amount;

    @ApiModelProperty("交易类型")
    private String tradeTypeName;

    @ApiModelProperty("交易状态")
    private String tradeStatusName;

    @ApiModelProperty("交易完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payFinishTime;

    @ApiModelProperty("费率类型")
    private String rateType;

    @ApiModelProperty("费率")
    private BigDecimal rate;

    @ApiModelProperty("手续费")
    private BigDecimal fee;


}