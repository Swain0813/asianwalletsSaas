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
 * @create: 2020-02-11 17:15
 **/
@Data
@ApiModel(value = "QfPayQuery请求实体", description = "QfPayQuery请求实体")
public class QfPayQueryDTO {

    @ApiModelProperty(value = "")
    public int merchantId;

    @ApiModelProperty(value = "")
    public String orderNum;

    @ApiModelProperty(value = "")
    public String outTradeNo;

    public QfPayQueryDTO() {
    }

    public QfPayQueryDTO(Channel channel, Orders orders) {
        this.merchantId = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.orderNum = orders.getChannelNumber();
        //this.outTradeNo =  orders.getId();
    }
    public QfPayQueryDTO(Channel channel, OrderRefund orderRefund) {
        this.merchantId = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.orderNum = orderRefund.getChannelNumber();
        //this.outTradeNo =  orderRefund.getId();
    }
}
