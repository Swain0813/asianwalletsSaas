package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "机构权益输入下拉框查询DTO", description = "机构权益输入下拉框查询DTO")
public class InstitutionRightsQueryDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

}