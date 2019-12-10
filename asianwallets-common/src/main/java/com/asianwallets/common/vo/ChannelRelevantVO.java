package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "通道关联信息输出实体", description = "通道关联信息输出实体")
public class ChannelRelevantVO {

    @ApiModelProperty(value = "通道id")
    private String channelId;

    @ApiModelProperty(value = "通道名称")
    private String channelName;
    @ApiModelProperty(value = "通道名称")
    private String channelCurrency;

    @ApiModelProperty(value = "通道名称")
    private String channelEnName;

    @ApiModelProperty(value = "权重")
    private String sort;

    @ApiModelProperty(value = "启用禁用")
    private Boolean channelEnabled;

    @ApiModelProperty
    private Boolean flag;

    @ApiModelProperty(value = "启用禁用")
    private List<BankReleVantVO> bankReleVantVOList;



}
