package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 查询可用余额
 */
@Data
@ApiModel(value = "查询可用余额", description = "查询可用余额")
public class SearchAvaBalDTO {


    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "币种")
    private String currency;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "变动类型")
    private String type;

    @ApiModelProperty(value = "账户类型")
    private String accountType;
}
