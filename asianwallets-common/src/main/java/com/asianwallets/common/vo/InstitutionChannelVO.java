package com.asianwallets.common.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
@ApiModel(value = "机构通道输出实体", description = "机构通道输出实体")
public class InstitutionChannelVO {

    @ApiModelProperty(value = "机构产品id")
    private String insProId;

    @ApiModelProperty(value = "通道id")
    private String channelId;

}
