package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易对账单英文输出实体", description = "交易对账单英文输出实体")
public class ExportTradeCheckAccountEnVO {

    @ApiModelProperty(value = "Trade Time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeTime;

    @ApiModelProperty(value = "Merchant Id")
    private String merchantId;

    @ApiModelProperty(value = "Merchant Name")
    private String merchantName;

    @ApiModelProperty(value = "currency")
    private String currency;

    @ApiModelProperty(value = "Total Trade Count")
    private Integer totalTradeCount;

    @ApiModelProperty(value = "Total Trade Amount")
    private BigDecimal totalTradeAmount;

    @ApiModelProperty(value = "Total Refund Count")
    private Integer totalRefundCount;

    @ApiModelProperty(value = "Total Refund Amount")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty(value = "fee")
    private BigDecimal fee;
}
