package com.asianwallets.common.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "机构产品通道输入实体", description = "机构产品通道输入实体")
public class InstitutionProductChannelDTO {

    @ApiModelProperty(value = "机构id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "产品id")
    private String productId;

    @ApiModelProperty(value = "通道id集合")
    private List<String> channelIdList;

    @ApiModelProperty(value = "产品排序")
    private Integer rank;

    @ApiModelProperty(value = "产品简称")
    private String productAbbrev;

    @ApiModelProperty(value = "产品详情logo")
    private String productDetailsLogo;

    @ApiModelProperty(value = "产品打印logo")
    private String productPrintLogo;

    @ApiModelProperty(value = "产品图片")
    private String productImg;

}
