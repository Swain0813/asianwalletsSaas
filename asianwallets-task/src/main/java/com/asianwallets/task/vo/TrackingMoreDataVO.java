package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "TrackingMore查询接口输出实体", description = "TrackingMore查询接口输出实体")
public class TrackingMoreDataVO {

    @ApiModelProperty(value = "页码")
    private String page;

    @ApiModelProperty(value = "每页数量")
    private String limit;

    @ApiModelProperty(value = "总条数")
    private String total;

    @ApiModelProperty(value = "items")
    private List<TrackingMoreItemsVO> items;


    public TrackingMoreDataVO() {

    }

}
