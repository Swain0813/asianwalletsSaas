package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 机构结算对账的英文版输出实体
 */
@Data
@ApiModel(value = "机构结算对账的英文版输出实体", description = "机构结算对账的英文版输出实体")
public class SettleCheckAccountEnVO {

    @ApiModelProperty(value = "Statement Date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "Currency")
    private String currency;

    @ApiModelProperty(value = "Institution Id")
    private String institutionCode;

    @ApiModelProperty(value = "Order Number")
    private int count;

    @ApiModelProperty(value = "Order Amount")
    private BigDecimal amount = BigDecimal.ZERO;

    @ApiModelProperty(value = "Fee")
    private BigDecimal fee = BigDecimal.ZERO;

    @ApiModelProperty(value = "Initial Amount")
    private BigDecimal initialAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "Final Amount")
    private BigDecimal finalAmount = BigDecimal.ZERO;
}
