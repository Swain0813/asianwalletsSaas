package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-01-02 10:42
 **/
@Data
public class RightsOrdersOutDTO {


    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "签名类型")
    private String signType;

    @ApiModelProperty(value = "签名")
    private String sign;
}
