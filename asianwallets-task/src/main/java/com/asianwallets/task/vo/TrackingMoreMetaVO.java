package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TrackingMore查询接口输出实体", description = "TrackingMore查询接口输出实体")
public class TrackingMoreMetaVO {

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "type")
    private String type;

    @ApiModelProperty(value = "message")
    private String message;


    public TrackingMoreMetaVO() {

    }

}
