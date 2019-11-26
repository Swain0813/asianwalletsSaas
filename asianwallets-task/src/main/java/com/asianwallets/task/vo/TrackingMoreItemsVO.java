package com.asianwallets.task.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TrackingMore查询接口输出实体", description = "TrackingMore查询接口输出实体")
public class TrackingMoreItemsVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "运单号")
    private String tracking_number;

    @ApiModelProperty(value = "运输商简码")
    private String carrier_code;

    @ApiModelProperty(value = "包裹状态")//delivered 成功签收
    private String status;

    @ApiModelProperty(value = "运单号最后一次自动查询时间")
    private String track_update;

    @ApiModelProperty(value = "运单号添加时间")
    private String created_at;

    @ApiModelProperty(value = "运单号最后一次自动查询时间")
    private String updated_at;

    @ApiModelProperty(value = "订单创建时间")
    private String order_create_time;

    @ApiModelProperty(value = "运单号添加时间")
    private String customer_email;

    @ApiModelProperty(value = "商品标题")
    private String title;

    @ApiModelProperty(value = "订单号")
    private String order_id;

    @ApiModelProperty(value = "商品备注")
    private String comment;

    @ApiModelProperty(value = "客户名称")
    private String customer_name;

    @ApiModelProperty(value = "运单号添加时间")
    private String archived;

    @ApiModelProperty(value = "运单号添加时间")
    private String original_country;

    @ApiModelProperty(value = "运单号添加时间")
    private String destination_country;

    @ApiModelProperty(value = "客户邮箱")
    private String itemTimeLength;

    @ApiModelProperty(value = "冻结余额")
    private String stayTimeLength;

    @ApiModelProperty(value = "描述")
    private String service_code;

    @ApiModelProperty(value = "冻结余额")
    private String status_info;

    @ApiModelProperty(value = "描述")
    private String substatus;

//    @ApiModelProperty(value = "描述")
//    private String origin_info;

//    @ApiModelProperty(value = "描述")
//    private String destination_info;

    @ApiModelProperty(value = "描述")
    private String lastEvent;

    @ApiModelProperty(value = "描述")
    private String lastUpdateTime;

    public TrackingMoreItemsVO() {

    }

}
