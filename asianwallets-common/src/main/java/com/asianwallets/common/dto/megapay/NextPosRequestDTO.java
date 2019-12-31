package com.asianwallets.common.dto.megapay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "NextPosCSB请求实体", description = "NextPosCSB请求实体")
public class NextPosRequestDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String einv;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "")
    private String return_url;

    @ApiModelProperty(value = "产品名")
    private String product;

    //以下不是上报通道参数
    @ApiModelProperty(value = "订单")
    private Orders orders;

    @ApiModelProperty(value = "通道")
    private Channel channel;

    public NextPosRequestDTO() {
    }

    public NextPosRequestDTO(Orders orders, Channel channel, String retURL) {
        //商户号
        this.merID = channel.getChannelMerchantId();
        //订单号
        this.einv = orders.getId();
        //标价金额,外币交易的支付金额精确到币种的最小单位,参数值不能带小数点。
        this.amt = String.valueOf(orders.getChannelAmount());//订单金额
        this.return_url = retURL;
        this.product = orders.getProductName();
        this.orders = orders;
        this.channel = channel;
    }

}
