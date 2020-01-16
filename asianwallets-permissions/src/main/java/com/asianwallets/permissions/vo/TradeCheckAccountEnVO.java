package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易对账总表输出实体
 */
@Data
@ApiModel(value = "交易对账总表输出实体", description = "交易对账总表输出实体")
public class TradeCheckAccountEnVO {

    @ApiModelProperty(value = "Institution Id")
    private String institutionCode;

    @ApiModelProperty("Initial Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    @ApiModelProperty("Final Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("Currency")
    private String currency;

    @ApiModelProperty("Total Order Amount")
    private BigDecimal totalTradeAmount;

    @ApiModelProperty("Total Order Number")
    private Integer totalTradeCount;

    @ApiModelProperty("Total Refund Amount")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty("Total Refund Number")
    private Integer totalRefundCount;

    @ApiModelProperty("Total Expenditure amount")
    private BigDecimal totalExpenditureAmount;

    @ApiModelProperty("Total Expenditure Count")
    private Integer totalExpenditureCount;

    @ApiModelProperty("Fee")
    private BigDecimal fee;
}
