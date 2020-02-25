package com.asianwallets.common.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "机构后台分润查询VO", description = "机构后台分润查询VO")
public class QueryAgencyShareBenefitVO {

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date createTime;

    @ApiModelProperty(value = "代理商编号")
    private String agentId;

    @ApiModelProperty(value = "代理商名称")
    private String agentName;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "代理商类型")// 1-渠道代理 2-商户代理
    private String agentType;

    @ApiModelProperty(value = "交易流水号")
    private String orderId;

    @ApiModelProperty(value = "订单币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "分润比例")
    private BigDecimal dividedRatio;

    @ApiModelProperty(value = "分润金额")
    private BigDecimal shareBenefit;

    @ApiModelProperty(value = "分润状态")//1:待分润，2：已分润
    private Byte isShare;

    @ApiModelProperty(value = "备注")
    private String remark;

}
