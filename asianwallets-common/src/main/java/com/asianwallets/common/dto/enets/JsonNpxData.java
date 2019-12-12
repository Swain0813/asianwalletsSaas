package com.asianwallets.common.dto.enets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class JsonNpxData {

    @JsonProperty("E103")
    private String posId;

    @JsonProperty("E104")
    private String txn;

    @JsonProperty("E107")
    private String edcBatchNumber;

    @JsonProperty("E201")
    private String sourceAmount;

    @JsonProperty("E202")
    private String sourceCurrency;

    @JsonProperty("E204")
    private String targetCurrencyReq;

    @JsonProperty("E601")
    private String cardType;

    @JsonProperty("F101")
    private String transactionId;

    @JsonProperty("F102")
    private String transactionType;

    @JsonProperty("F103")
    private String transactionStatus;

    @JsonProperty("F104")
    private String transactionTimestamp;

    @JsonProperty("F111")
    private String originalSourceAmount;

    @JsonProperty("F112")
    private String sourceCurrencyResponse;

    @JsonProperty("F113")
    private String originalTargetAmount;

    @JsonProperty("F114")
    private String targetCurrency;

    @JsonProperty("F116")
    private String targetCurrencyLongText;


    @JsonProperty("F117")
    private String conversionRate;

    @JsonProperty("F121")
    private String sourceAmountRes;

    @JsonProperty("F122")
    private String targetAmount;

    @JsonProperty("F200")
    private String courencyGroups;

    @JsonProperty("F201")
    private String targetCurrencyRes;

    @JsonProperty("F202")
    private String targetCurrencyText;

    @JsonProperty("F203")
    private String targetCurrencyIso;

    @JsonProperty("F204")
    private String exchangeRate;

    @JsonProperty("F209")
    private String receiptHeader1;

    @JsonProperty("F210")
    private String receiptHeader2;

    @JsonProperty("F211")
    private String receiptHeader3;

    @JsonProperty("F212")
    private String receiptHeader4;

    @JsonProperty("F213")
    private String expressPayIndicator;

    @JsonProperty("F214")
    private String expressPayMessage;

    @JsonProperty("F215")
    private String acquirerId;

    @JsonProperty("F216")
    private String terminalAcquirerId;

    @JsonProperty("F217")
    private String paymentTypeId;

    @JsonProperty("F218")
    private String sofDetails;

    @JsonProperty("F998")
    private String uposErrorMessage;

    @JsonProperty("F999")
    private String uposErrorCode;

    public JsonNpxData() {
    }
}
