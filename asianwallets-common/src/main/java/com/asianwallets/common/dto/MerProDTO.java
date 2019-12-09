package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description: 商户分配通道实体
 * @author: YangXu
 * @create: 2019-12-09 16:08
 **/
@Data
@ApiModel(value = "商户分配通道实体", description = "商户分配通道实体")
public class MerProDTO {


    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @NotNull(message = "50002")
    @ApiModelProperty(value = "产品")
    List<ProdChannelDTO> productList;
}
