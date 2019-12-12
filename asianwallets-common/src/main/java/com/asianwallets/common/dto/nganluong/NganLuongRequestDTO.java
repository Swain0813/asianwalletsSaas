package com.asianwallets.common.dto.nganluong;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.MD5;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: NGANLUONG通道请求实体
 * @author: YangXu
 * @create: 2019-06-18 10:26
 **/
@Data
@ApiModel(value = "NGANLUONG通道请求实体", description = "NGANLUONG通道请求实体")
public class NganLuongRequestDTO {

    @ApiModelProperty(value = "商户id")
    private int merchant_id;

    @ApiModelProperty(value = "商户密码")
    private String merchant_password;

    @ApiModelProperty(value = "版本号")
    private String version = "3.1";

    @ApiModelProperty(value = "SetExpressCheckout")
    private String function;

    @ApiModelProperty(value = "")
    private String order_code;

    @ApiModelProperty(value = "")
    private String receiver_email;

    @ApiModelProperty(value = "金额")
    private int total_amount;

    @ApiModelProperty(value = "支付方式")
    private String payment_method;

    @ApiModelProperty(value = "Bank id")
    private String bank_code;

    //@ApiModelProperty(value = "")
    //private String payment_type;
    //
    //@ApiModelProperty(value = "")
    //private String order_description;
    //
    //@ApiModelProperty(value = "")
    //private int tax_amount;
    //
    //@ApiModelProperty(value = "")
    //private int discount_amount;
    //
    //@ApiModelProperty(value = "")
    //private int fee_shipping;
    //
    @ApiModelProperty(value = "Payment Succeeded Page")
    private String return_url;
    //
    @ApiModelProperty(value = "Payment Canceled Page URL")
    private String cancel_url;
    //
    //@ApiModelProperty(value = "")
    //private int time_limit  = 1440;

    @ApiModelProperty(value = "")
    private String buyer_fullname;

    @ApiModelProperty(value = "")
    private String buyer_email;

    @ApiModelProperty(value = "")
    private String buyer_mobile;

    //@ApiModelProperty(value = "")
    //private String buyer_address;
    //
    //@ApiModelProperty(value = "")
    //private String cur_code;
    //
    //@ApiModelProperty(value = "")
    //private String lang_code;
    //
    //@ApiModelProperty(value = "")
    //private String affiliate_code;
    //
    //@ApiModelProperty(value = "")
    //private String total_item;
    //
    //@ApiModelProperty(value = "")
    //private String item_name1;
    //
    //@ApiModelProperty(value = "")
    //private String item_quantity1;
    //
    //@ApiModelProperty(value = "")
    //private String item_amount1;
    //
    //@ApiModelProperty(value = "")
    //private String item_url1;


    public NganLuongRequestDTO() {
    }

    public NganLuongRequestDTO(Channel channel, Orders orders, String returnUrl, String cancelUrl) {
        String[] s = channel.getChannelMerchantId().split("\\|");
        this.merchant_id = Integer.parseInt(channel.getChannelMerchantId().split("\\|")[0]);
        this.merchant_password = MD5.MD5Encode(channel.getMd5KeyStr());
        this.version = "3.1";
        this.function = "SetExpressCheckout";
        this.order_code = orders.getId();
        this.receiver_email = channel.getChannelMerchantId().split("\\|")[1];
        this.total_amount = orders.getTradeAmount().intValue();
        this.payment_method = channel.getPayCode();
        this.bank_code = orders.getIssuerId();
        this.buyer_fullname = null;
        this.buyer_email = orders.getDraweeEmail();
        this.buyer_mobile = null;
        this.return_url = returnUrl;
        this.cancel_url = cancelUrl;
    }
}
