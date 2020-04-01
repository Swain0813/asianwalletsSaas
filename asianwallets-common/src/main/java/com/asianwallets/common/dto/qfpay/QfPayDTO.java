package com.asianwallets.common.dto.qfpay;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-11 17:58
 **/
@Data
@ApiModel(value = "QfPay请求实体", description = "QfPay请求实体")
public class QfPayDTO {

    @ApiModelProperty(value = "订单ID")
    public String orderId;

    @ApiModelProperty(value = "请求IP")
    public String reqIp;

    @ApiModelProperty(value = "通道")
    public Channel channel;

    @ApiModelProperty(value = "CSB实体")
    public QfPayCSBDTO qfPayCSBDTO;

    @ApiModelProperty(value = "BSC实体")
    public QfPayBSCDTO qfPayBSCDTO;

    @ApiModelProperty(value = "查询实体")
    public QfPayQueryDTO qfPayQueryDTO;

    @ApiModelProperty(value = "退款实体")
    public QfPayRefundDTO qfPayRefundDTO;

}
