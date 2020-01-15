package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 机构系统结算对账
 */
@Data
@ApiModel(value = "机构结算对账输入参数", description = "机构结算对账输入参数")
public class TradeCheckAccountSettleExportDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "对账时间")
    private String checkDate;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "语言")
    private String language;
}
