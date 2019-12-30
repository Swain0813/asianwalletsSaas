package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.text.DecimalFormat;

/**
 * @description: 支付宝线线上网站支付
 * @author: YangXu
 * @create: 2019-06-17 17:27
 **/
@Data
@ApiModel(value = "支付宝线线上网站支付", description = "支付宝线线上网站支付")
public class AliPayWebDTO {

    @ApiModelProperty(value = "支付接口名称")
    private String service;

    @ApiModelProperty(value = "渠道商户号")
    private String partner;

    @ApiModelProperty(value = "编码格式")
    private String _input_charset;

    @ApiModelProperty(value = "异步回调地址")
    private String notify_url;

    @ApiModelProperty(value = "页面通知地址")
    private String return_url;

    @ApiModelProperty(value = "默认12小时，最大15天。此为买家登陆到完成支付的有效时间")
    private String timeout_rule;

    @ApiModelProperty(value = "境外商户交易号")
    private String out_trade_no;

    @ApiModelProperty(value = "商品标题")
    private String subject;

    @ApiModelProperty(value = "使用新接口需要加这个支付宝产品code")
    private String product_code;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "")
    private String seller_id;

    @ApiModelProperty(value = "")
    private String seller_email;

    @ApiModelProperty(value = "商品描述")
    private String body;

    @ApiModelProperty(value = "")
    private String show_url;

    @ApiModelProperty(value = "结算币种")
    private String currency;

    @ApiModelProperty(value = "")
    private String trans_currency;

    @ApiModelProperty(value = "")
    private String quantity;

    @ApiModelProperty(value = "")
    private String goods_detail;

    @ApiModelProperty(value = "")
    private String extend_params;

    @ApiModelProperty(value = "")
    private String it_b_pay;

    @ApiModelProperty(value = "")
    private String passback_parameters;

    @ApiModelProperty(value = "md5Key")
    private String md5KeyStr;

    @ApiModelProperty(value = "二级商户行业")
    private String secondary_merchant_industry;

    @ApiModelProperty(value = "由支付机构给二级商户分配的唯一ID")
    private String secondary_merchant_id;

    @ApiModelProperty(value = "二级商户名称")
    private String secondary_merchant_name;

    @ApiModelProperty(value = "")
    private String store_id;

    @ApiModelProperty(value = "")
    private String store_name;

    @ApiModelProperty(value = "")
    private String terminal_id;

    @ApiModelProperty(value = "")
    private String sys_service_provider_id;

    @ApiModelProperty(value = "机构订单号")
    private String institution_order_id;

    @ApiModelProperty(value = "请求Ip")
    private String reqIp;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public AliPayWebDTO() {
    }

    public AliPayWebDTO(Orders orders, Channel channel) {
        this.service = "create_forex_trade";
        this.partner = channel.getChannelMerchantId();
        this._input_charset = "UTF-8";
        this.subject = orders.getProductName();
        this.md5KeyStr = channel.getMd5KeyStr();
        this.notify_url = channel.getNotifyServerUrl();//后台回调地址
        this.return_url = channel.getNotifyBrowserUrl();//浏览器回调地址
        this.out_trade_no = orders.getId();
        this.currency = orders.getTradeCurrency();
        this.body = null;
        this.timeout_rule = "12h";
        this.secondary_merchant_industry = "5812";
        this.secondary_merchant_id = "20170828500054";
        this.secondary_merchant_name = "zhtest";
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        this.amt = decimalFormat.format(orders.getChannelAmount());
        this.channel = channel;
        this.institution_order_id = orders.getMerchantOrderId();
        this.reqIp = orders.getReqIp();
    }

}
