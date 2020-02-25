package com.asianwallets.common.dto;
import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;


@Data
@ApiModel(value = "机构分润输入参数", description = "机构分润输入参数")
public class QueryAgencyShareBenefitDTO extends BasePageHelper {

    @ApiModelProperty(value = "交易流水号")
    private String orderId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "机构编号")
    private String institutionCode;

    @ApiModelProperty(value = "分润状态")//1:待分润，2：已分润
    private Byte isShare;

    @ApiModelProperty(value = "起始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "代理商编号")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "代理商类型")//1-渠道代理 2-商户代理
    private String agentType;

    @ApiModelProperty(value = "订单币种")
    private String tradeCurrency;

}
