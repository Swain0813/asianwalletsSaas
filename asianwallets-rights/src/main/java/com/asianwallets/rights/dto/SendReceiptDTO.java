package com.asianwallets.rights.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "发券DTO", description = "发券DTO")
public class SendReceiptDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "团购号")
    private String dealId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "平台流水号")
    private String systemOrderId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "手机号")
    private String mobileNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "发券数量")
    private Integer sendCount;

}
