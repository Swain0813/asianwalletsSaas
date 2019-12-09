package com.asianwallets.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-07-05 14:25
 **/
@Data
@ApiModel(value = "通道银行实体", description = "通道银行实体")
public class ChaBankRelVO {
    @ApiModelProperty(value = "通道产品id")
    private String chabankId;

    @ApiModelProperty(value = "银行Code")
    private String sort;


}
