package com.asianwallets.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-20 15:20
 **/
@Data
public class AvailableBanks {

    @ApiModelProperty(value = "")
    public String bank_code;

    @ApiModelProperty(value = "")
    public String collection_type;

    @ApiModelProperty(value = "")
    public String bank_account_number;

    @ApiModelProperty(value = "")
    public BigDecimal transfer_amount;

    @ApiModelProperty(value = "")
    public String bank_branch;

    @ApiModelProperty(value = "")
    public String account_holder_name;

    @ApiModelProperty(value = "")
    public BigDecimal identity_amount;

}
