package com.asianwallets.common.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;


@Data
@ApiModel(value = "机构通道输出实体", description = "机构通道输出实体")
public class InstitutionChannelQueryVO {

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelCnName;

    @ApiModelProperty(value = "通道支持银行")
    private List<BankVO> bankNames;

    @ApiModelProperty(value = "通道币种")
    private String currency;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean supportRefundState;

    @ApiModelProperty(value = "是否退还收单手续费")//1-退还,2-不退还,3-仅限当日退还
    private Byte refundingIsReturnFee;

    @ApiModelProperty(value = "优先级")
    private String sort;

    @ApiModelProperty(value = "通道状态")
    private Boolean enabled;
}
