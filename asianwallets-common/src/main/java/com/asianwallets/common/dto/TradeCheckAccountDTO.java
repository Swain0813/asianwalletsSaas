package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("交易对账输入实体")
public class TradeCheckAccountDTO extends BasePageHelper {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "交易时间")
    private String tradeTime;

    @ApiModelProperty(value = "对账时间")
    private String checkDate;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "语言")
    private String language;
}
