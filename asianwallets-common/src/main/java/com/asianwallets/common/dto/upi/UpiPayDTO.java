package com.asianwallets.common.dto.upi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 14:47
 **/
@Data
@ApiModel(value = "支付DTO", description = "支付DTO")
public class UpiPayDTO {

    @ApiModelProperty(value = "String")
    private String version;

    @ApiModelProperty(value = "交易代码")
    private String trade_code;

    @ApiModelProperty(value = "银行代码")
    private String bank_code;

    @ApiModelProperty(value = "商户号")
    private String agencyId;

    @ApiModelProperty(value = "子商户编号")
    private String child_merchant_no;

    @ApiModelProperty(value = "终端号")
    private String terminal_no;

    @ApiModelProperty(value = "商户订单号")
    private String order_no;

    @ApiModelProperty(value = "商户退款单号")
    private String refund_no;

    @ApiModelProperty(value = "订单交易金额")
    private String amount;

    @ApiModelProperty(value = "交易币种")
    private String currency_type;

    @ApiModelProperty(value = "清算币种")
    private String sett_currency_type;

    @ApiModelProperty(value = "用户付款二维码内容")
    private String auth_code;

    @ApiModelProperty(value = "卡安全码")
    private String cvn2;

    @ApiModelProperty(value = "有效期")
    private String valid;

    @ApiModelProperty(value = "产品名称")
    private String product_name;

    @ApiModelProperty(value = "产品描述")
    private String product_desc;

    @ApiModelProperty(value = "产品类型")
    private String product_type;

    @ApiModelProperty(value = "用户名称")
    private String user_name;

    @ApiModelProperty(value = "用户证件类型")
    private String user_cert_type;

    @ApiModelProperty(value = "用户证件号码")
    private String user_cert_no;

    @ApiModelProperty(value = "用户银行证件号码")
    private String user_bank_card_no;


    @ApiModelProperty(value = "同步通知地址")
    private String return_url;

    @ApiModelProperty(value = "异步通知地址")
    private String notify_url;

    @ApiModelProperty(value = "客户端IP")
    private String client_ip;
}
