package com.asianwallets.common.dto.enets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@Data
public class TransactionDetail implements Serializable {

    private static final long serialVersionUID = -1512270040084536951L;

    @JsonProperty("amount")
    private String targetAmount;

    @JsonProperty("currency")
    private String targetCurrency;

    @JsonProperty("approval_code")
    private String approvalCode;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("stan")
    private String stan;

    @JsonProperty("retrieval_ref")
    private String retrievalRefNo;

    @JsonProperty("payment_type")
    private String paymentType;

    public TransactionDetail() {

    }


}
