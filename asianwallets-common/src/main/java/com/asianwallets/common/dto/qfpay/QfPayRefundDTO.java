package com.asianwallets.common.dto.qfpay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-11 17:18
 **/
@Data
@ApiModel(value = "QfPayCSB请求实体", description = "QfPayCSB请求实体")
public class QfPayRefundDTO {

    @ApiModelProperty(value = "")
    public int merchantId;

    @ApiModelProperty(value = "")
    public int cash;

    @ApiModelProperty(value = "")
    public String orderNum;
    @ApiModelProperty(value = "")
    public String outTradeNo;

    public QfPayRefundDTO() {
    }

    public QfPayRefundDTO(Channel channel, OrderRefund orderRefund) {
        this.merchantId = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.cash =orderRefund.getTradeAmount().intValue();
        this.orderNum = orderRefund.getChannelNumber();
        this.outTradeNo = orderRefund.getId();
    }
    public QfPayRefundDTO(Channel channel, Orders orders) {
        this.merchantId = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.cash =orders.getTradeAmount().intValue();
        this.orderNum = orders.getChannelNumber();
        //撤销是没有生成订单 拿R+机构订单号作为退款单号
        this.outTradeNo = "R"+orders.getInstitutionOrderId();
    }
}
