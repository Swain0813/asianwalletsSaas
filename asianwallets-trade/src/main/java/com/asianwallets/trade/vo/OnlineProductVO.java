package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "产品输出实体", description = "产品输出实体")
public class OnlineProductVO {

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "产品编号")
    private Integer productCode;

    @ApiModelProperty(value = "产品币种")
    private String productCurrency;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "费率")
    private BigDecimal rate;

    @ApiModelProperty(value = "费率类型")
    private String rateType;

    @ApiModelProperty(value = "单笔限额")
    private BigDecimal limitAmount;

    @ApiModelProperty(value = "日交易总笔数限额")
    private Integer dailyTradingCount;

    @ApiModelProperty(value = "浮动率")
    private BigDecimal ipFloatRate;

    @ApiModelProperty(value = "附加值")
    private BigDecimal ipAddValue;

    @ApiModelProperty(value = "结算周期")
    private String settleCycle;

    @ApiModelProperty(value = "通道")
    private List<OnlineChannelVO> channelList;
}
