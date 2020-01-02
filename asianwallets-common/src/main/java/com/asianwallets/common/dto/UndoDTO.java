package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 撤销订单的输入参数
 */
@Data
@ApiModel(value = "撤销订单请求参数", description = "撤销订单请求参数")
public class UndoDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "设备编号")
    private String imei;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "设备操作员")
    private String operatorId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名类型")//1为RSA 2为MD5
    private String signType;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "签名")
    private String sign;
}
