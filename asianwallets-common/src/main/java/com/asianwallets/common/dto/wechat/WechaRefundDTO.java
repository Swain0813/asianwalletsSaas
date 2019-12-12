package com.asianwallets.common.dto.wechat;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.UUIDHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 微信退款请求实体
 * @author: YangXu
 * @create: 2019-06-26 14:48
 **/
@Data
@ApiModel(value = "微信退款请求实体", description = "微信退款请求实体")
public class WechaRefundDTO {

    @ApiModelProperty(value = "公众号id")
    private String apikey;

    @ApiModelProperty(value = "公众号id")
    private String appid;

    @ApiModelProperty(value = "公众号id")
    private String sub_appid;

    @ApiModelProperty(value = "")
    private String sign_type;

    @ApiModelProperty(value = "")
    private String mch_id;

    @ApiModelProperty(value = "")
    private String sub_mch_id;

    @ApiModelProperty(value = "")
    private String nonce_str;

    @ApiModelProperty(value = "")
    private int total_fee;

    @ApiModelProperty(value = "")
    private int refund_fee;

    @ApiModelProperty(value = "")
    private String refund_fee_type;

    @ApiModelProperty(value = "")
    private String refund_account;

    @ApiModelProperty(value = "")
    private String transaction_id;

    @ApiModelProperty(value = "")
    private String out_trade_no;

    @ApiModelProperty(value = "")
    private String out_refund_no;

    @ApiModelProperty(value = "")
    private String refund_desc;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public WechaRefundDTO() {
    }

    public WechaRefundDTO(OrderRefund orderRefund, Channel channel) {
        this.apikey = channel.getMd5KeyStr();
        this.appid = "wx14e049b9320bccca";
        //this.sub_appid = "";
        this.sign_type = "MD5";
        this.mch_id = channel.getChannelMerchantId();
        this.sub_mch_id = "66104046";
        this.nonce_str = UUIDHelper.getRandomString(32);
        this.total_fee = orderRefund.getAmount().multiply(new BigDecimal(100)).intValue();
        this.refund_fee = orderRefund.getTradeAmount().multiply(new BigDecimal(100)).intValue();
        this.refund_fee_type = orderRefund.getTradeCurrency();
        this.refund_account = "REFUND_SOURCE_UNSETTLED_FUNDS";
        this.transaction_id = orderRefund.getChannelNumber();
        this.out_trade_no = orderRefund.getOrderId();
        this.out_refund_no = orderRefund.getId();
        this.refund_desc = "退款";
        this.channel = channel;
    }

    public WechaRefundDTO(Orders orders, Channel channel) {
        this.apikey = channel.getMd5KeyStr();
        this.appid = "wx14e049b9320bccca";
        //this.sub_appid = "";
        this.sign_type = "MD5";
        this.mch_id = channel.getChannelMerchantId();
        this.sub_mch_id = "66104046";
        this.nonce_str = UUIDHelper.getRandomString(32);
        this.total_fee = orders.getAmount().multiply(new BigDecimal(100)).intValue();
        this.refund_fee = orders.getTradeAmount().multiply(new BigDecimal(100)).intValue();
        this.refund_fee_type = orders.getTradeCurrency();
        this.refund_account = "REFUND_SOURCE_UNSETTLED_FUNDS";
        this.transaction_id = orders.getChannelNumber();
        this.out_trade_no = orders.getId();
        this.out_refund_no = IDS.uuid2();
        this.refund_desc = "退款";
        this.channel = channel;
    }
}
