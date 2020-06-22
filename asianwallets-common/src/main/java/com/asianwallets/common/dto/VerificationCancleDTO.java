package com.asianwallets.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 权益核销DTO
 * @author: YangXu
 * @create: 2019-12-30 13:56
 **/
@Data
public class VerificationCancleDTO {

    @ApiModelProperty(value = "机构编号")
    @NotNull(message = "50002")
    private String institutionId;

    @ApiModelProperty(value = "二级商户编号")
    @NotNull(message = "50002")
    private String merchantId;

    @ApiModelProperty(value = "票券编号")
    @NotNull(message = "50002")
    private List<String> ticketId;

    @ApiModelProperty(value = "机构订单号")
    @NotNull(message = "50002")
    private String orderNo;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "核销发起的请求时间")
    @NotNull(message = "50002")
    private String requestTime;

    @ApiModelProperty(value = "返回地址")
    @NotNull(message = "50002")
    private String serverUrl;

    @ApiModelProperty(value = "签名类型")
    private String signType;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "备注")
    private String remark;

}
