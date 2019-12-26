package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Pos币种输出实体", description = "Pos币种输出实体")
public class PosCurrencyVO {

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "币种默认值")
    private String defaults;

}
