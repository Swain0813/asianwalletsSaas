package com.asianwallets.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-06-20 15:23
 **/
@Data
public class AvailableRetailOutlets {


    @ApiModelProperty(value = "")
    public String retail_outlet_name;

    @ApiModelProperty(value = "")
    public String payment_code;

    @ApiModelProperty(value = "")
    public String transfer_amount;

}
