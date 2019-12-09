package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "银行输出实体", description = "银银行输出实体行实体")
public class BankVO {

    @ApiModelProperty(value = "产品id")
    private String bankID;

    @ApiModelProperty(value = "银行Code")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行国家")
    private String bankCountry;

    @ApiModelProperty(value = "币种")
    private String bankCurrency;
}
