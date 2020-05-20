package com.asianwallets.common.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "通道输入实体", description = "通道输入实体")
public class ChannelInfoDTO {

    @ApiModelProperty(value = "通道id")
    String channelId;

    @ApiModelProperty(value = "权重")
    List<BankInfoDTO> bankList;
}
