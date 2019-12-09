package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-04 16:39
 **/
@Data
public class BankDTO extends BasePageHelper {

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "国家")
    private String bankCountry;

    @ApiModelProperty(value = "币种")
    private String id;
    private String issuerId;
    private String bankCurrency;
    private String currency;
    private boolean enabled;
    private String bankCode;
}
