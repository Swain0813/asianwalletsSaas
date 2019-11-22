package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TrackingMore查询接口输出实体", description = "TrackingMore查询接口输出实体")
public class TrackingMoreVO {

    @ApiModelProperty(value = "meta")
    private TrackingMoreMetaVO meta;

    @ApiModelProperty(value = "data")
    private TrackingMoreDataVO data;

    public TrackingMoreVO() {

    }

}
