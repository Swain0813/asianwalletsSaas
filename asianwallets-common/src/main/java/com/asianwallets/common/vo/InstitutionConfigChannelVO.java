package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "通道输出实体", description = "通道输出实体")
public class InstitutionConfigChannelVO {

    @ApiModelProperty(value = "通道id")
    private String channelId;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelCnName;

}
