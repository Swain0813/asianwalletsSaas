package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: yangshanlong@asianwallets.com
 * @create: 2020/6/22 12:17
 **/
@Data
@ApiModel(value = "发券DTO", description = "发券DTO")
public class SendTicketDTO {
    @NotNull(message = "50002")
    @ApiModelProperty(value = "团购号")
    private String dealId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "平台流水号")
    private String systemOrderId;

    @ApiModelProperty(value = "手机号")
    private String mobileNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "发券数量")
    private Integer sendCount;
}
