package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@ApiModel(value = "代理商渠道VO", description = "代理商渠道VO")
public class AgentChannelsVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "通道币种")
    private String channelCurrency;

    @ApiModelProperty(value = "通道费率类型")
    private String channelFeeType;

    @ApiModelProperty(value = "费率")
    private BigDecimal channelRate;

    @ApiModelProperty(value = "退款")
    private Boolean channelCanRefund;

    @ApiModelProperty(value = "退还手续费(1-退还,2-不退还,3-仅限当日退还)")
    private Integer channelCanRefundFee;

    @ApiModelProperty(value = "通道状态")
    private Boolean channelStatus;

    @ApiModelProperty(value = "分润比例")
    private BigDecimal dividedRatio;

    @ApiModelProperty(value = "通道支持银行")
    private List<String> channelAvailableBanks;


}
