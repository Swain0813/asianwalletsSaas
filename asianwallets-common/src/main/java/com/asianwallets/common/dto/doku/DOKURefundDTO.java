package com.asianwallets.common.dto.doku;

import com.asianwallets.common.entity.Channel;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-13 10:45
 **/
@Data
@ApiModel(value = "DOKU退款请求实体", description = "DOKU退款请求实体")
public class DOKURefundDTO {

    @ApiModelProperty(value = "Given by DOKU")
    @JsonProperty("MALLID")
    private String MALLID;

    @ApiModelProperty(value = "Given by DOKU, if not using Chain, use value : NA")
    private String CHAINMERCHANT;

    @ApiModelProperty(value = "New Refund Transaction ID from Merchant")
    private String REFIDMERCHANT;

    @ApiModelProperty(value = "Original Transaction ID from Merchant")
    private String TRANSIDMERCHANT;

    @ApiModelProperty(value = "Transaction number from Bank")
    private String APPROVALCODE;

    @ApiModelProperty(value = "Total VOID/REFUND amount")
    private String AMOUNT;

    @ApiModelProperty(value = "SO3166,numeric code")
    private String CURRENCY;

    @ApiModelProperty(value = "01 = Full Refund 02 = Partial Refund Credit 03 = Partial Refund Debit")
    private String REFUNDTYPE;

    @ApiModelProperty(value = "Merchant can use this parameter for additional validation,DOKU will return the value in other response process")
    private String SESSIONID;

    @ApiModelProperty(value = "")
    private String WORDS;

    @ApiModelProperty(value = "Reason for refund")
    private String REASON;

    @ApiModelProperty(value = "Channel")
    private Channel channel;
}
