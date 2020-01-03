package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "提款银行卡DTO")
public class WithdrawalBankDTO {

    @ApiModelProperty(name = "商户编号")
    private String merchantId;

    @ApiModelProperty(name = "提款金额")
    private BigDecimal amount;

    @ApiModelProperty(name = "提款币种")//账户币种
    private String currency;

    @ApiModelProperty(name = "银行卡币种")//银行卡币种
    private String bankCodeCurrency;

    @ApiModelProperty(name = "提款银行账户")
    private String bankAccountCode;
}
