package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MChannelVO", description = "MChannelVO")
public class MChannelVO {

    @ApiModelProperty(value = "CID")
    public String cid;

    @ApiModelProperty(value = "创建者")
    private String cName;
}