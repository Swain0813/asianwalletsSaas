package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-10 15:41
 **/
@Data
@ApiModel(value = "商户关联信息输出实体", description = "商户关联信息输出实体")
public class MerchantRelevantVO {

    @ApiModelProperty(value = "机构id")
    private String institutionId;

    @ApiModelProperty(value = "机构名称")
    private String institutionName;

    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "产品关联信息")
    private List<ProductRelevantVO> productRelevantVOS;
}
