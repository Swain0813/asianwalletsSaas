package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author XuWenQi
 * @Date: 2019/6/10 15:02
 * @Description: help2Pay浏览器回调实体
 */
@Data
@ApiModel(value = "help2Pay浏览器回调实体", description = "help2Pay浏览器回调实体")
public class Help2PayCallbackDTO {

    @ApiModelProperty(value = "通道商户号")
    private String Merchant;

    @ApiModelProperty(value = "币种")
    private String Currency;

    @ApiModelProperty(value = "机构号")
    private String Customer;

    @ApiModelProperty(value = "订单id")
    private String Reference;

    @ApiModelProperty(value = "订单金额")
    private String Amount;

    @ApiModelProperty(value = "备注")
    private String Note;

    @ApiModelProperty(value = "订单时间")
    private String Datetime;

    @ApiModelProperty(value = "语言")
    private String Language;

    @ApiModelProperty(value = "签名")
    private String Key;

    @ApiModelProperty(value = "IP")
    private String ClientIP;

    @ApiModelProperty(value = "状态")
    private String Status;

    @ApiModelProperty(value = "返回的交易号")
    private String ID;

    @ApiModelProperty(value = "statementDate")
    private String StatementDate;
}
