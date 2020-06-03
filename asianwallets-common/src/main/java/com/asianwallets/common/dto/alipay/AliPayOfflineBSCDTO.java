package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * @description: 支付宝线下BSC请求实体
 * @author: XuWenQi
 * @create: 2019-06-14 14:51
 **/
@Data
@ApiModel(value = "支付宝线下BSC请求实体", description = "支付宝线下BSC请求实体")
public class AliPayOfflineBSCDTO {

    @ApiModelProperty(value = "支付接口名称")
    private String service;

    @ApiModelProperty(value = "渠道商户号")
    private String partner;

    @ApiModelProperty(value = "编码格式")
    private String _input_charset;

    @ApiModelProperty(value = "渠道商户号 Same value with partner ID")
    private String alipay_seller_id;

    @ApiModelProperty(value = "产品名称")
    private String trans_name;

    @ApiModelProperty(value = "商户订单号")
    private String partner_trans_id;

    @ApiModelProperty(value = "交易币种")
    private String currency;

    @ApiModelProperty(value = "交易金额")
    private String trans_amount;

    @ApiModelProperty(value = "支付宝条码")
    private String buyer_identity_code;

    @ApiModelProperty(value = "barcode表示条码 qrcode表示二维码")
    private String identity_code_type;

    @ApiModelProperty(value = "固定值OVERSEAS_MBARCODE_PAY")
    private String biz_product;

    @ApiModelProperty(value = "扩展参数")
    private String extend_info;

    //以下不是上报通道的参数
    @ApiModelProperty(value = "订单")
    private Orders orders;

    @ApiModelProperty(value = "通道")
    private Channel channel;


    public AliPayOfflineBSCDTO() {
    }

    public AliPayOfflineBSCDTO(Orders orders, Channel channel, String buyer_identity_code) {
        JSONObject extendJson = new JSONObject();
        extendJson.put("secondary_merchant_id", orders.getSubMerchantCode());
        extendJson.put("secondary_merchant_name", orders.getSubMerchantName());
        extendJson.put("secondary_merchant_industry", orders.getMerchantIndustry());
        extendJson.put("store_id", StringUtils.isEmpty(orders.getShopCode())?"zh001":orders.getShopCode());
        extendJson.put("store_name", StringUtils.isEmpty(orders.getShopName())?"zhstore":orders.getShopName());
        this.extend_info = extendJson.toString();
        this.service = "alipay.acquire.overseas.spot.pay";
        //渠道商户号
        this.partner = channel.getChannelMerchantId()==null?"2088421920790891":channel.getChannelMerchantId();
        this._input_charset = "UTF-8";
        //渠道商户号
        this.alipay_seller_id = channel.getChannelMerchantId()==null?"2088421920790891":channel.getChannelMerchantId();
        this.trans_name = StringUtils.isEmpty(orders.getProductName()) ? "Commodity" : orders.getProductName();//产品名称
        this.partner_trans_id = orders.getId();//订单号
        this.currency = orders.getTradeCurrency();//币种
        this.trans_amount = String.valueOf(orders.getChannelAmount());
        this.buyer_identity_code = buyer_identity_code;//支付宝条码
        this.identity_code_type = "barcode";
        this.biz_product = "OVERSEAS_MBARCODE_PAY";
        this.orders = orders;
        this.channel = channel;
    }
}
