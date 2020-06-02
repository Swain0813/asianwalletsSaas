package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 银行卡撤销冲正接口的输入参数
 */
@Data
@ApiModel(value = "银行卡撤销冲正接口的输入参数", description = "银行卡撤销冲正接口的输入参数")
public class BankCardUndoDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "银行卡号")
    private String userBankCardNo;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "cvv2")
    private String cvv2;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "卡有效期")
    private String valid;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "磁道信息")
    private String trackData;

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

    @ApiModelProperty(value = "语言")
    private String language;
}
