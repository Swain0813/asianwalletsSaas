package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "机构权益查询API DTO", description = "机构权益查询API DTO")
public class InstitutionRightsInfoApiDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动开始时间")
    private String activityStartTime;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "活动结束时间")
    private String activityEndTime;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名方式")//1为RSA 2为MD5
    private String signType;
}