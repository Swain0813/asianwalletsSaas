package com.asianwallets.trade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "线上查询订单接口输入实体", description = "线上查询订单接口输入实体")
public class OnlineCheckOrdersDTO {

    @NotNull(message = "50002")
    @ApiModelProperty(value = "商户编号")
    private String merchantId;

    @ApiModelProperty(value = "系统订单流水号")
    private String referenceNo;

    @ApiModelProperty(value = "机构订单号")
    private String orderNo;

    @ApiModelProperty(value = "交易开始时间")
    private String startDate;

    @ApiModelProperty(value = "交易结束时间")
    private String endDate;

    @ApiModelProperty(value = "交易状态 1-待支付 2-交易中 3-交易成功 4-交易失败 5-已过期")
    private Byte txnStatus;

    @ApiModelProperty(value = "退款状态")
    private Byte refundStatus;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名")
    private String sign;

    @NotNull(message = "52008")
    @ApiModelProperty(value = "签名方式")
    private String signType;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "页码")
    public Integer pageNum;

    @ApiModelProperty(value = "每页条数")
    public Integer pageSize;
}
