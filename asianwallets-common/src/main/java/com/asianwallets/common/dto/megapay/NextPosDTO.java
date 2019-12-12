package com.asianwallets.common.dto.megapay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: nextPos请求实体
 * @author: YangXu
 * @create: 2019-06-12 15:50
 **/
@Data
@ApiModel(value = "MegaPay通道请求实体", description = "MegaPay通道请求实体")
public class NextPosDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String einv;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "页面返回地址")
    private String return_url;

    @ApiModelProperty(value = "产品名")
    private String product;
}
