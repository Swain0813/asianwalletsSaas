package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "通道导出输出实体", description = "通道导出输出实体")
public class ChannelExport {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelCnName;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "通道币种")
    private String currency;

    @ApiModelProperty(value = "通道最小限额")
    private BigDecimal limitMinAmount;

    @ApiModelProperty(value = "通道最大限额")
    private BigDecimal limitMaxAmount;

    @ApiModelProperty(value = "通道状态")
    private String enabledStr;

    @ApiModelProperty(value = "payCode")
    private String payCode;

    @ApiModelProperty(value = "优先级")
    private String sort;

    @ApiModelProperty(value = "结算周期")
    private String settleCycle;

    @ApiModelProperty(value = "是否支持退款")
    private String supportRefundStateStr;

    @ApiModelProperty(value = "通道手续费类型")
    private String channelFeeType;

    @ApiModelProperty(value = "通道费率")
    private String channelRate;

    @ApiModelProperty(value = "通道费率最小值")
    private BigDecimal channelMinRate;

    @ApiModelProperty(value = "通道费率最大值")
    private BigDecimal channelMaxRate;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "是否退还收单手续费")
    private String refundingIsReturnFee;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "更改者")
    private String modifier;

    @ApiModelProperty(value = "备注")
    private String remark;

}
