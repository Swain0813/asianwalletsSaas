package com.asianwallets.common.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "机构产品通道输出实体", description = "机构产品通道输出实体")
public class InstitutionProductChannelVO {

    @ApiModelProperty(value = "机构id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "产品简称")
    private String productAbbrev;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "支付方式ID")
    private String payTypeId;

    @ApiModelProperty(value = "交易类型")
    private Byte transType;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "机构通道集合")
    private List<InstitutionChannelVO> institutionChannelVOList;

}
