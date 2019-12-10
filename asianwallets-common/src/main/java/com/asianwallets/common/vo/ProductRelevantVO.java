package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "产品关联信息输出实体", description = "产品关联信息输出实体")
public class ProductRelevantVO {

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "产品简称")
    private String productAbbrev;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "交易场景")
    private Integer tradeDirection; //交易场景：1-线上pc 2-线上移动 2-线下移动

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "币种")
    private String productCurrency;

    @ApiModelProperty(value = "产品关联信息")
    private List<ChannelRelevantVO> channelRelevantVOS;

}
