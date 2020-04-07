package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-04-07 14:02
 **/
@Data
@ApiModel(value = "分润统计查询", description = "分润统计查询")
public class ShareBenefitStatisticalDTO {

    @ApiModelProperty(value = "起始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "代理商编号")
    private String agentId;
}
