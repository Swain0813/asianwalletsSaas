package com.asianwallets.common.dto.doku;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-12 10:39
 **/
@Data
@ApiModel(value = "DOKU收单请求实体", description = "DOKU收单请求实体")
public class DOKURequestDTO {

    @ApiModelProperty(value = "Given by DOKU")
    @JsonProperty("MALLID")
    private String MALLID;

    @ApiModelProperty(value = "Given by DOKU, if not using Chain, use value : NA")
    private String CHAINMERCHANT;

    @ApiModelProperty(value = "Total amount. Eg: 10000.00")
    private String AMOUNT;

    @ApiModelProperty(value = "Total amount. Eg: 10000.00")
    private String PURCHASEAMOUNT;

    @ApiModelProperty(value = "Transaction ID from Merchant")
    private String TRANSIDMERCHANT;

    @ApiModelProperty(value = "Default is SALE")
    private String PAYMENTTYPE;

    @ApiModelProperty(value = "签名")
    private String WORDS;

    @ApiModelProperty(value = "订单时间 YYYYMMDDHHMMSS")
    private String REQUESTDATETIME;

    @ApiModelProperty(value = "ISO3166 , numeric code")
    private String CURRENCY;

    @ApiModelProperty(value = "ISO3166 , numeric code")
    private String PURCHASECURRENCY;

    @ApiModelProperty(value = "Merchant can use this parameter for additional validation, DOKU will return the value in other response process")
    private String SESSIONID;

    @ApiModelProperty(value = "Travel Arranger Name / Buyer name")
    private String NAME;

    @ApiModelProperty(value = "Customer email")
    private String EMAIL;

    @ApiModelProperty(value = "payment channel code list O")
    private String PAYMENTCHANNEL;

    @ApiModelProperty(value = "")
    private String BASKET;


    public DOKURequestDTO() {
    }

    public DOKURequestDTO(Orders orders, Channel channel) {
        this.MALLID = channel.getChannelMerchantId();
        this.CHAINMERCHANT = "NA";
        this.AMOUNT = String.valueOf(orders.getChannelAmount());
        this.PURCHASEAMOUNT = String.valueOf(orders.getChannelAmount());
        this.TRANSIDMERCHANT = orders.getId();
        this.PAYMENTTYPE = "SALE";
        this.REQUESTDATETIME = DateToolUtils.getReqDateyyyyMMddHHmmss(new Date());
        this.CURRENCY = "360";
        this.PURCHASECURRENCY = "360";
        this.SESSIONID = "SESSIONID";
        this.NAME = orders.getPayerName();
        this.EMAIL = orders.getPayerEmail();
        this.PAYMENTCHANNEL = channel.getIssuerId();
        this.BASKET = "ITEM1,10000.00,2,20000.00;ITEM2,20000.00,4,80000.00";
    }


    public DOKURequestDTO(OrderRefund orderRefund, Channel channel) {
        this.MALLID = channel.getChannelMerchantId();
        this.CHAINMERCHANT = "NA";
        this.TRANSIDMERCHANT = orderRefund.getOrderId();
        this.SESSIONID = "SESSIONID";
    }
}
