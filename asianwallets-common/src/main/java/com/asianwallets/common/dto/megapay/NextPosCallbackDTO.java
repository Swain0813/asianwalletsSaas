package com.asianwallets.common.dto.megapay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "NextPos回调实体", description = "NextPos回调实体")
public class NextPosCallbackDTO {

    @ApiModelProperty(value = "订单号")
    private String einv;

    @ApiModelProperty(value = "响应码")
    private String refCode;

    @ApiModelProperty(value = "金额")
    private String amt;

    @ApiModelProperty(value = "code")
    private String transactionID;

    @ApiModelProperty(value = "签名")
    private String mark;

    @ApiModelProperty(value = "订单状态")
    private String status;

    @ApiModelProperty(value = "通道加密MD5key")
    private String merRespPassword;

    @ApiModelProperty(value = "支付code")
    private String merRespID;

    @ApiModelProperty(value = "payCode")
    private String payCode;
}
