package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "机构后台分润查询英文版", description = "机构后台分润查询英文版")
public class QueryAgencyShareBenefitEnVO {

    @ApiModelProperty(value = "Creation Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "Agent Id")
    private String agentId;

    @ApiModelProperty(value = "Agent Name")
    private String agentName;

    @ApiModelProperty(value = "Agent Type")// 1-渠道代理 2-商户代理
    private String agentType;

    @ApiModelProperty(value = "Order Id")
    private String orderId;

    @ApiModelProperty(value = "merchantOrderId")
    private String merchantOrderId;

    @ApiModelProperty(value = "channelOrderId")
    private String channelOrderId;

    @ApiModelProperty(value = "Order Currency")
    private String tradeCurrency;

    @ApiModelProperty(value = "Order Amount")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "Fee")
    private Double fee;

    @ApiModelProperty(value = "productCode")
    private String productCode;

    @ApiModelProperty(value = "productName")
    private String productName;

    @ApiModelProperty(value = "channelCode")
    private String channelCode;

    @ApiModelProperty(value = "channelName")
    private String channelName;

    @ApiModelProperty(value = "Divided Ratio")
    private BigDecimal dividedRatio;

    @ApiModelProperty(value = "Share Benefit Amount")
    private Double shareBenefit;

    @ApiModelProperty(value = "Share Benefit Status")//1:待分润，2：已分润
    private Byte isShare;

    @ApiModelProperty(value = "Remark")
    private String remark;

}
