package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "产品通道输入实体", description = "产品通道输入实体")
public class ProdChannelDTO {

    @ApiModelProperty(value = "产品id")
    String productId;

    @ApiModelProperty(value = "通道")
    List<ChannelInfoDTO> channelList;
}
