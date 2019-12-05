package com.asianwallets.common.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "机构通道输入实体", description = "机构通道输入实体")
public class InstitutionChannelDTO {

    @ApiModelProperty(value = "机构产品id")
    private String insProId;

    @ApiModelProperty(value = "通道id")
    private String channelId;

}
