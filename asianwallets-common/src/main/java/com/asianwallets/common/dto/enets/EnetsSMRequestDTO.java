package com.asianwallets.common.dto.enets;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.ChannelsOrder;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.utils.UUIDHelper;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: enets线下扫码请求实体
 * @author: YangXu
 * @create: 2019-06-03 15:35
 **/
@Data
@ApiModel(value = "enets线下扫码请求实体", description = "enets线下扫码请求实体")
public class EnetsSMRequestDTO {

    @JsonProperty("retrieval_ref")
    private String retrievalRef;

    @JsonProperty("mti")
    private String mti;

    @JsonProperty("txn_identifier")
    private String searchId;

    @JsonProperty("process_code")
    private String processCode;

    @JsonProperty("amount")
    private String targetAmount;

    @JsonProperty("stan")
    private String stan;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("entry_mode")
    private String entryMode;

    @JsonProperty("condition_code")
    private String conditionCode;

    @JsonProperty("institution_code")
    private String institutionCode;

    @JsonProperty("card_seq_number")
    private String cardSequenceNumber;

    @JsonProperty("track2_data")
    private String track2Data;

    @JsonProperty("icc_data")
    private String iccData;

    @JsonProperty("pin")
    private String pin;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("approval_code")
    private String approvalCode;

    @JsonProperty("host_tid")
    private String tid;

    @JsonProperty("host_mid")
    private String mid;

    @JsonProperty("pay_details")
    private String payDetails;

    @JsonProperty("SOF_uri")
    private String sofUri;

    @JsonProperty("user_data")
    private String userData;

    @JsonProperty("sig")
    private String sig;

    @JsonProperty("transmission_time")
    private String transmissionTime;

    @JsonProperty("settlement_date")
    private String settlementDate;

    @JsonProperty("capture_date")
    private String captureDate;

    @JsonProperty("nii")
    private String nii;

    @JsonProperty("acceptor_name")
    private String acceptorName;

    @JsonProperty("loyalty_data")
    private String loyaltyData;

    @JsonProperty("invoice_ref")
    private String invoiceRef;

    @JsonProperty("npx_data")
    private JsonNpxData npxData;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("cvv")
    private String cvv;

    @JsonProperty("search_count")
    private Integer searchCount;

    @JsonProperty("communication_data")
    private ArrayList<JsonCommunicationData> communicationData;

    @JsonProperty("transactions")
    private ArrayList<TransactionDetail> transactions;

    @JsonProperty("getQRCode")
    private String getQRCode;

    @JsonProperty("qr_code")
    private String qRCode;

    public EnetsSMRequestDTO() {
    }

    public EnetsSMRequestDTO(Orders orders, Channel channel, String callBackUrl) {
        this.tid = "37066801";
        this.mid = channel.getChannelMerchantId();
        this.retrievalRef = UUIDHelper.getRandomString(12);
        this.mti = "0200";
        this.processCode = "990000";
        long transactionAmountCents = orders.getTradeAmount().multiply(new BigDecimal(100)).longValue();
        this.targetAmount = String.format("%012d", transactionAmountCents);
        this.stan = IDS.randomNumber(6);
        this.transactionTime = DateUtil.format(orders.getMerchantOrderTime(), "HHmmss"); //转换为字符串格式
        this.transactionDate = DateUtil.format(orders.getMerchantOrderTime(), "MMdd"); //转换为字符串格式
        this.entryMode = "000";
        this.conditionCode = "85";
        this.institutionCode = "20000000001";
        JsonNpxData npxData = new JsonNpxData();
        npxData.setPosId(tid);//tid
        npxData.setSourceAmount(String.format("%08d", transactionAmountCents));
        npxData.setSourceCurrency(orders.getTradeCurrency());
        this.npxData = npxData;
        ArrayList<JsonCommunicationData> communicationDatas = new ArrayList<>();
        JsonCommunicationData communicationData = new JsonCommunicationData();
        communicationData.setCommunicationCategory("URL");
        communicationData.setCommunicationDestination(callBackUrl);
        communicationData.setCommunicationType("https_proxy");
        Map<String, String> addon = new HashMap<>();
        //external_API_keyID
        addon.put("external_API_keyID", "8bc63cde-2647-4a78-ac75-d5f534b56047");
        communicationDatas.add(communicationData);
        this.communicationData = communicationDatas;
        this.getQRCode = "Y";
        //this.searchId = searchId;
        //this.cardSequenceNumber = cardSequenceNumber;
        //this.track2Data = track2Data;
        //this.iccData = iccData;
        //this.pin = pin;
        //this.responseCode = responseCode;
        //this.approvalCode = approvalCode;
        //this.payDetails = payDetails;
        //this.sofUri = sofUri;
        //this.userData = userData;
        //this.sig = sig;
        //this.transmissionTime = transmissionTime;
        //this.settlementDate = settlementDate;
        //this.captureDate = captureDate;
        //this.nii = nii;
        //this.acceptorName = acceptorName;
        //this.loyaltyData = loyaltyData;
        //this.invoiceRef = invoiceRef;
        //this.callbackUrl = callbackUrl;
        //this.cvv = cvv;
        //this.searchCount = searchCount;
        //this.transactions = transactions;
        //this.qRCode = qRCode;
    }

    /**
     * 退款订单功能构造
     *
     * @param channel
     */
    public EnetsSMRequestDTO(OrderRefund orderRefund, Channel channel, ChannelsOrder channelsOrder) {
        this.mti = "0200";
        this.processCode = "201000";
        this.tid = "37066801";
        long transactionAmountCents = orderRefund.getTradeAmount().multiply(new BigDecimal(100)).longValue();
        this.targetAmount = String.format("%012d", transactionAmountCents);
        this.stan = channelsOrder.getRemark2();
        this.transactionTime = DateUtil.format(new Date(), "HHmmss");
        this.transactionDate = DateUtil.format(new Date(), "MMdd");
        this.entryMode = "012";
        this.conditionCode = "85";
        this.institutionCode = "20000000001";
        this.retrievalRef = channelsOrder.getRemark1();
        this.mid = channel.getChannelMerchantId();
        this.tid = "37066801";
        this.searchId = channelsOrder.getRemark3();
        JsonNpxData npxData = new JsonNpxData();
        npxData.setPosId(tid);
        npxData.setTxn(channelsOrder.getRemark3());
        long total_fee = orderRefund.getTradeAmount().multiply(new BigDecimal(100)).longValue();
        npxData.setSourceAmount(String.format("%08d", total_fee));
        npxData.setSourceCurrency(orderRefund.getTradeCurrency());
        this.npxData = npxData;
    }
}
