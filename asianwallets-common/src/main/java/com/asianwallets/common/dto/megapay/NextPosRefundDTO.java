package com.asianwallets.common.dto.megapay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: Nextpos退款请求实体
 * @author: XuWenQi
 * @create: 2019-08-09 13:49
 **/
@Data
@ApiModel(value = "Nextpos退款请求实体", description = "Nextpos退款请求实体")
public class NextPosRefundDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "退款类型")//1 : Full Refund  2 : Partial Refund
    private String refundType;

    @ApiModelProperty(value = "The transaction amount")
    private String originalAmt;

    @ApiModelProperty(value = "Refund Amount, it can be either Full Amount or Partial Amount")
    private String amt;

    @ApiModelProperty(value = "It is the refCode of the Payment response from NextPos (Refer to Session 1)")
//"Note: The value is the converted ASCII String"
    private String tradeNo;

    @ApiModelProperty(value = "签名")
    private String mark;

    @ApiModelProperty(value = "merRespPassword")
    private String merRespPassword;

    @ApiModelProperty(value = "merRespID")
    private String merRespID;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public NextPosRefundDTO() {
    }

    public NextPosRefundDTO(OrderRefund orderRefund, Channel channel) {
        this.merID = channel.getChannelMerchantId();
        this.orderId = orderRefund.getOrderId();
        this.refundType = String.valueOf(orderRefund.getRefundType());
        this.originalAmt = String.valueOf(orderRefund.getTradeAmount());
        this.tradeNo = orderRefund.getSign();
        this.merRespPassword = channel.getMd5KeyStr();
        this.merRespID = channel.getPayCode();
        if(orderRefund.getFeePayer()==1){
            this.amt = String.valueOf(orderRefund.getTradeAmount());
        }else {
            this.amt = String.valueOf(orderRefund.getTradeAmount().add(orderRefund.getRefundOrderFeeTrade()));
        }
        this.channel = channel;
    }
}
