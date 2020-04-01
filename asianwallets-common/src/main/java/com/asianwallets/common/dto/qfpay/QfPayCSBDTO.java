package com.asianwallets.common.dto.qfpay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-11 16:59
 **/
@Data
@ApiModel(value = "QfPayCSB请求实体", description = "QfPayCSB请求实体")
public class QfPayCSBDTO {

    @ApiModelProperty(value = "商户号")
    public Integer merchantId;

    @ApiModelProperty(value = "需要收款的金额")
    public Integer cash;

    @ApiModelProperty(value = "支付方式")
    public String payType;

    //@ApiModelProperty(value = "优惠券代码")
    //public String couponCode;

    @ApiModelProperty(value = "系统订单号")
    public String outTradeNo;

    @ApiModelProperty(value = "备注")
    public String comment;

    public QfPayCSBDTO() {

    }

    public QfPayCSBDTO(Orders orders, Channel channel) {
        this.merchantId = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.cash = orders.getTradeAmount().intValue();
        this.payType = channel.getPayCode();
        //this.couponCode = "";
        this.outTradeNo = orders.getId();
    }
}
