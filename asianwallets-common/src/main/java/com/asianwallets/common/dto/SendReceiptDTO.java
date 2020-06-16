package com.asianwallets.common.dto;

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

    @ApiModelProperty(value = "平台流水号")
    private String systemOrderId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "发送类型")//1-短信平台 2-邮件平台
    private Byte sendType;

    @ApiModelProperty(value = "手机号")
    private String mobileNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "发送内容")
    private String content;

    @ApiModelProperty(value = "发券数量")
    private Integer sendCount;

    @ApiModelProperty(value = "发放人")
    private String userName;

}
