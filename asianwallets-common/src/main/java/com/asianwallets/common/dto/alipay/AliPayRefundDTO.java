package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.AlipayCore;
import com.asianwallets.common.utils.IDS;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 支付宝线下退款请求实体
 * @author: XuWenQi
 * @create: 2019-06-21 15:10
 **/
@Data
@ApiModel(value = "支付宝线下退款请求实体", description = "支付宝线下退款请求实体")
public class AliPayRefundDTO {

    @ApiModelProperty(value = "接口名称")
    private String service;

    @ApiModelProperty(value = "编码格式")
    private String _input_charset;

    @ApiModelProperty(value = "签约的支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成")
    private String partner;

    @ApiModelProperty(value = "支付宝合作商户网站唯一订单号（确保在商户系统中唯一）")
    private String partner_refund_id;

    @ApiModelProperty(value = "商家退款编号")
    private String partner_trans_id;

    @ApiModelProperty(value = "退款金额")
    private String refund_amount;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "签名方式")
    private String sign_type;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public AliPayRefundDTO() {
    }

    public AliPayRefundDTO(OrderRefund orderRefund, Channel channel) {
        this.service = "alipay.acquire.overseas.spot.refund";
        this._input_charset = "UTF-8";
        this.partner = channel.getChannelMerchantId();
        this.partner_refund_id = orderRefund.getId();
        this.partner_trans_id = orderRefund.getOrderId();
        this.refund_amount = String.valueOf(orderRefund.getTradeAmount());
        this.currency = orderRefund.getTradeCurrency();
        this.sign_type = "MD5";
        Map<String, String> reqmap = new HashMap<>();
        reqmap.put("service", this.service);
        reqmap.put("_input_charset", this._input_charset);
        reqmap.put("partner", this.partner);
        reqmap.put("partner_refund_id", this.partner_refund_id);// 商家退款编号
        reqmap.put("partner_trans_id", this.partner_trans_id);//原订单号
        reqmap.put("refund_amount", this.refund_amount);
        reqmap.put("currency", this.currency);
        Map<String, String> signMap = AlipayCore.buildRequestPara(reqmap, channel.getMd5KeyStr());
        this.sign = signMap.get("sign");
        this.channel = channel;
    }

    public AliPayRefundDTO(Orders orders, Channel channel) {
        this.service = "alipay.acquire.overseas.spot.refund";
        this._input_charset = "UTF-8";
        this.partner = channel.getChannelMerchantId();
        this.partner_refund_id = IDS.uuid2();
        this.partner_trans_id = orders.getId();
        this.refund_amount = String.valueOf(orders.getTradeAmount());
        this.currency = orders.getTradeCurrency();
        this.sign_type = "MD5";
        Map<String, String> reqmap = new HashMap<>();
        reqmap.put("service", this.service);
        reqmap.put("_input_charset", this._input_charset);
        reqmap.put("partner", this.partner);
        reqmap.put("partner_refund_id", this.partner_refund_id);// 商家退款编号
        reqmap.put("partner_trans_id", this.partner_trans_id);//原订单号
        reqmap.put("refund_amount", this.refund_amount);
        reqmap.put("currency", this.currency);
        Map<String, String> signMap = AlipayCore.buildRequestPara(reqmap, channel.getMd5KeyStr());
        this.sign = signMap.get("sign");
        this.channel = channel;
    }
}
