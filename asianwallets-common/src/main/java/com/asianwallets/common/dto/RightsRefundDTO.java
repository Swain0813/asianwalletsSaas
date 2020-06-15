package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @description: 权益退款实体
 * @author: YangXu
 * @create: 2020-01-02 10:21
 **/
@Data
public class RightsRefundDTO {

    @ApiModelProperty(value = "票券编号")
    private String ticketId;

    @ApiModelProperty(value = "机构编号")
    private String institutionId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "票券来源")
    private String systemName;

    @ApiModelProperty(value = "请求时间")
    private String requestTime;

    @ApiModelProperty(value = "签名类型")
    private String signType;

    @ApiModelProperty(value = "签名")
    private String sign;

}
