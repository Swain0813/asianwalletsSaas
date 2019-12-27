package com.asianwallets.trade.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "EnetsPos回调实体", description = "EnetsPos回调实体")
public class EnetsPosCallbackDTO {

    @ApiModelProperty(value = "")
    private String stan;

    @ApiModelProperty(value = "")
    private String retrieval_ref;

    @ApiModelProperty(value = "")
    private String txn_identifier;

    @ApiModelProperty(value = "")
    private String response_code;

    public EnetsPosCallbackDTO() {
    }

    public EnetsPosCallbackDTO(String stan, String retrieval_ref, String txn_identifier, String response_code) {
        this.stan = stan;
        this.retrieval_ref = retrieval_ref;
        this.txn_identifier = txn_identifier;
        this.response_code = response_code;
    }
}
