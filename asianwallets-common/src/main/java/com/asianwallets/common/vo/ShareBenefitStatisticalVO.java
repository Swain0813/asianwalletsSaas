package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-04-07 14:03
 **/
@Data
@ApiModel(value = "分润统计查询", description = "分润统计查询")
public class ShareBenefitStatisticalVO {

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "分润金额")
    private BigDecimal amount;
}
