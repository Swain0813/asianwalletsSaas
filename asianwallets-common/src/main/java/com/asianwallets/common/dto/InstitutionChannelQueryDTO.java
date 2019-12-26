package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "机构通道查询输入参数", description = "机构通道查询输入参数")
public class InstitutionChannelQueryDTO {

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelCnName;


    @ApiModelProperty(value = "通道币种")
    private String currency;

    @ApiModelProperty(value = "启用禁用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean supportRefundState;

    @ApiModelProperty(value = "是否退还收单手续费")//1-退还,2-不退还,3-仅限当日退还
    private Byte refundingIsReturnFee;
}
