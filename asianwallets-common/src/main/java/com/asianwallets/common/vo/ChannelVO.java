package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "通道详情输出实体一览页面用", description = "通道详情输出实体一览页面用")
public class ChannelVO {

    @ApiModelProperty(value = "通道id")
    public String id;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelCnName;


    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "通道币种")
    private String currency;


    @ApiModelProperty(value = "通道最小限额")
    private String limitMinAmount;

    @ApiModelProperty(value = "通道最大限额")
    private String limitMaxAmount;

    @ApiModelProperty(value = "通道状态")
    private Boolean enabled;

    @ApiModelProperty(value = "是否支持退款")
    private Boolean supportRefundState;

    @ApiModelProperty(value = "是否退还收单手续费")//1-退还,2-不退还,3-仅限当日退还
    private Byte refundingIsReturnFee;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更改者")
    private String modifier;

}
