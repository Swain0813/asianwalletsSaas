package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "Xe汇率查询接口输出实体", description = "Xe汇率查询接口输出实体")
public class XeRateResponseVO {

    @ApiModelProperty(value = "买入汇率")
    private BigDecimal mid;

    @ApiModelProperty(value = "目标币种")
    private String quotecurrency;

}
