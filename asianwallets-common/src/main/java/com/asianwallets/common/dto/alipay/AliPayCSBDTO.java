package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * @description: 支付宝线下CSB请求实体
 * @author: YangXu
 * @create: 2019-06-17 14:37
 **/
@Data
@ApiModel(value = "支付宝线下CSB请求实体", description = "支付宝线下CSB请求实体")
public class AliPayCSBDTO {

    @ApiModelProperty(value = "支付接口名称")
    private String service;

    @ApiModelProperty(value = "渠道商户号")
    private String partner;

    @ApiModelProperty(value = "编码格式")
    private String _input_charset;

    @ApiModelProperty(value = "异步通知地址")
    private String notify_url;

    @ApiModelProperty(value = "The time format is ：yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    @ApiModelProperty(value = "The time format is ：yyyy-MM-dd HH:mm:ss")
    private String terminal_timestamp;

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;

    @ApiModelProperty(value = "交易主题")
    private String subject;

    @ApiModelProperty(value = "")
    private String product_code;

    @ApiModelProperty(value = "交易订单总价=商品单价*数量")
    private String total_fee;

    @ApiModelProperty(value = "Unique Alipay user ID corresponding to Seller’s Alipay account")
    private String seller_id;

    @ApiModelProperty(value = "")
    private String seller_email;

    @ApiModelProperty(value = "")
    private String body;

    @ApiModelProperty(value = "")
    private String show_url;

    @ApiModelProperty(value = "银行产品结算币种")
    private String currency;

    @ApiModelProperty(value = "交易币种")
    private String trans_currency;

    @ApiModelProperty(value = "")
    private String quantity;

    @ApiModelProperty(value = "商品详情")
    private String goods_detail;

    @ApiModelProperty(value = "扩展参数")
    private String extend_params;

    @ApiModelProperty(value = "")
    private String it_b_pay;

    @ApiModelProperty(value = "")
    private String passback_parameters;

    @ApiModelProperty(value = "")
    private String md5KeyStr;

    @ApiModelProperty(value = "")
    private String secondary_merchant_industry;

    @ApiModelProperty(value = "")
    private String secondary_merchant_id;

    @ApiModelProperty(value = "")
    private String secondary_merchant_name;

    @ApiModelProperty(value = "")
    private String store_id;

    @ApiModelProperty(value = "")
    private String store_name;

    @ApiModelProperty(value = "")
    private String terminal_id;

    @ApiModelProperty(value = "")
    private String sys_service_provider_id;

    @ApiModelProperty(value = "订单")
    private Orders orders;

    @ApiModelProperty(value = "通道")
    private Channel channel;

    public AliPayCSBDTO() {
    }

    public AliPayCSBDTO(Orders orders, Channel channel, String notifyUrl) {
        this.service = "alipay.acquire.precreate";
        this.partner = channel.getChannelMerchantId();
        this.md5KeyStr = channel.getMd5KeyStr();
        this._input_charset = "UTF-8";
        this.notify_url = notifyUrl;
        this.timestamp = DateToolUtils.getReqDateG(new Date());
        this.terminal_timestamp = DateToolUtils.getReqDateG(new Date());
        this.out_trade_no = orders.getId();
        this.subject = orders.getProductName() == null ? "Commodity" : orders.getProductName();
        this.product_code = "OVERSEAS_MBARCODE_PAY";
        this.total_fee = orders.getChannelAmount().toString();
        this.seller_id = null;
        this.seller_email = null;
        this.body = null;
        this.show_url = null;
        this.currency = orders.getTradeCurrency();
        this.trans_currency = orders.getTradeCurrency();
        this.quantity = null;
        this.goods_detail = null;
        this.extend_params = null;
        this.it_b_pay = null;
        this.passback_parameters = null;
        this.secondary_merchant_industry=orders.getMerchantIndustry();
        this.secondary_merchant_id=orders.getSubMerchantCode();
        this.secondary_merchant_name=orders.getSubMerchantName();
        this.store_id = StringUtils.isEmpty(orders.getShopCode())?orders.getSubMerchantCode():orders.getShopCode();
        this.store_name = StringUtils.isEmpty(orders.getShopName())?orders.getSubMerchantName():orders.getShopName();
        this.terminal_id = null;
        this.sys_service_provider_id = null;
        this.orders = orders;
        this.channel = channel;
    }
}
