package com.asianwallets.common.dto;

import com.asianwallets.common.base.BasePageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "代理商渠道DTO", description = "代理商渠道DTO")
public class AgentChannelsDTO extends BasePageHelper {

    @ApiModelProperty(value = "代理商户号")
    private String agentId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "渠道状态")
    private Boolean channelStatus;

    @ApiModelProperty(value = "渠道币种")
    private String channelCurrency;

    @ApiModelProperty(value = "语言")
    private String language;

}
