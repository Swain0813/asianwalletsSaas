package com.asianwallets.permissions.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "交易对账单中文输出实体", description = "交易对账单中文输出实体")
public class ExportTradeCheckAccountVO {

    @ApiModelProperty(value = "交易时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date tradeTime;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "交易币种")
    private String currency;

    @ApiModelProperty(value = "收单总笔数")
    private Integer totalTradeCount;

    @ApiModelProperty(value = "收单总金额")
    private BigDecimal totalTradeAmount;

    @ApiModelProperty(value = "退款总笔数")
    private Integer totalRefundCount;

    @ApiModelProperty(value = "退款总金额")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

}