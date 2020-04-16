package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Doku线上网银服务器回调实体", description = "Doku线上网银服务器回调实体")
public class DokuBrowserCallbackDTO {

    @ApiModelProperty(value = "订单金额")
    private String AMOUNT;

    @ApiModelProperty(value = "订单号")
    private String TRANSIDMERCHANT;

    @ApiModelProperty(value = "签名")
    private String WORDS;

    @ApiModelProperty(value = "0000: Success, others Failed")
    private String STATUSCODE;

    @ApiModelProperty(value = "See payment channel code list")
    private String PAYMENTCHANNEL;

    @ApiModelProperty(value = "DOKU will return the value from Payment Request.")
    private String SESSIONID;

    @ApiModelProperty(value = "Virtual Account identifier for VA transaction. Has value if using Channel that has payment code")
    private String PAYMENTCODE;

    @ApiModelProperty(value = "ISO3166 , numeric code")
    private String CURRENCY;

    @ApiModelProperty(value = "ISO3166 , numeric code")
    private String PURCHASECURRENCY;
}
