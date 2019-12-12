package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 支付宝线下撤销请求实体
 * @author: XuWenQi
 * @create: 2019-06-28 9:55
 **/
@Data
@ApiModel(value = "支付宝线下撤销请求实体", description = "支付宝线下撤销请求实体")
public class AliPayCancelDTO {

    @ApiModelProperty(value = "接口名称")
    private String service;

    @ApiModelProperty(value = "渠道商户号")
    private String partner;

    @ApiModelProperty(value = "字符集")
    private String _input_charset;

    @ApiModelProperty(value = "支付宝合作商户网站唯一订单号（确保在商户系统中唯一）")
    private String out_trade_no;

    @ApiModelProperty(value = "当前时间戳")
    private String timestamp;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public AliPayCancelDTO() {
    }

    public AliPayCancelDTO(OrderRefund orderRefund, Channel channel) {
        this.service = "";
        this.partner = channel.getChannelMerchantId();
        this._input_charset = "UTF-8";
        this.out_trade_no = orderRefund.getOrderId();
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.md5KeyStr = channel.getMd5KeyStr(); //用来加密的密钥
        this.channel = channel;
    }
}
