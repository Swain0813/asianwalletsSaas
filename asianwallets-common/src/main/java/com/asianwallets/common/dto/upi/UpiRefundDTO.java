package com.asianwallets.common.dto.upi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-03 17:07
 **/
@Data
@ApiModel(value = "退款DTO", description = "退款DTO")
public class UpiRefundDTO {

    @ApiModelProperty(value = "String")
    private String version;

    @ApiModelProperty(value = "交易代码")
    private String trade_code;

    @ApiModelProperty(value = "商户号")
    private String agencyId;

    @ApiModelProperty(value = "子商户编号")
    private String child_merchant_no;

    @ApiModelProperty(value = "终端号")
    private String terminal_no;

    @ApiModelProperty(value = "商户订单号")
    private String order_no;

    @ApiModelProperty(value = "商户退款订单号")
    private String refund_no;

    @ApiModelProperty(value = "退款金额")
    private String refund_amount;

    @ApiModelProperty(value = "交易币种")
    private String currency_type;

    @ApiModelProperty(value = "清算币种")
    private String sett_currency_type;

    @ApiModelProperty(value = "异步通知地址")
    private String notify_url;

    @ApiModelProperty(value = "分账信息")
    private String split;

}
