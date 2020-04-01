package com.asianwallets.trade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "通道输出实体", description = "通道输出实体")
public class OnlineChannelVO {

    @ApiModelProperty(value = "通道id")
    private String channelId;

    @ApiModelProperty(value = "产品id")
    private String channelProductId;

    @ApiModelProperty(value = "通道币种")
    private String channelCurrency;

    @ApiModelProperty(value = "通道币种图标")
    private String channelCurrencyIcon;

    @ApiModelProperty(value = "通道中文名称")
    private String channelCnName;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "通道logo")
    private String channelImg;

    @ApiModelProperty(value = "通道支付方式CODE")
    private String channelPayType;

    @ApiModelProperty(value = "通道支付方式名称")
    private String channelPayTypeName;

    @ApiModelProperty(value = "不同币种的默认值")
    private String defaultValue;

    @ApiModelProperty(value = "银行")
    private List<BankReleVantVO> bankReleVantVOList;
}