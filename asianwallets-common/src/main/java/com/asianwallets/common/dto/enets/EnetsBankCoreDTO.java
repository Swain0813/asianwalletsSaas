package com.asianwallets.common.dto.enets;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: XuWenQi
 * @create: 2019-06-03 10:30
 **/

@Data
@ApiModel(value = "enets网银通道请求实体", description = "enets网银通道请求实体")
public class EnetsBankCoreDTO {

    @ApiModelProperty(value = "")
    private String netsMid;

    @ApiModelProperty(value = "")
    private String tid;

    @ApiModelProperty(value = "")
    private String submissionMode;

    @ApiModelProperty(value = "订单金额")
    private String txnAmount;

    @ApiModelProperty(value = "订单流水号")
    private String merchantTxnRef;

    @ApiModelProperty(value = "")
    private String merchantTxnDtm;

    @ApiModelProperty(value = "")
    private String paymentType;

    @ApiModelProperty(value = "订单币种")
    private String currencyCode;

    @ApiModelProperty(value = "")
    private String paymentMode;

    @ApiModelProperty(value = "")
    private String merchantTimeZone;

    @ApiModelProperty(value = "浏览器回调地址")
    private String b2sTxnEndURL;

    @ApiModelProperty(value = "")
    private String b2sTxnEndURLParam;

    @ApiModelProperty(value = "服务器回调地址")
    private String s2sTxnEndURL;

    @ApiModelProperty(value = "")
    private String s2sTxnEndURLParam;

    @ApiModelProperty(value = "")
    private String clientType;

    @ApiModelProperty(value = "")
    private String supMsg;

    @ApiModelProperty(value = "")
    private String netsMidIndicator;

    @ApiModelProperty(value = "")
    private String ipAddress;

    @ApiModelProperty(value = "")
    private String language;


    public EnetsBankCoreDTO() {
    }

    public EnetsBankCoreDTO(Orders orders, Channel channel, String b2sTxnEndURL, String s2sTxnEndURL, String amt) {
        this.netsMid = channel.getChannelMerchantId();
        this.tid = "192.168.124.64";//119.23.136.80
        this.submissionMode = "B";
        this.txnAmount = amt;//交易金额
        this.merchantTxnRef = orders.getId();//id
        this.merchantTxnDtm = DateToolUtils.getReqDateH(orders.getInstitutionOrderTime());//订单时间
        this.paymentType = "SALE";
        this.currencyCode = orders.getTradeCurrency();//交易币种
        this.paymentMode = "DD";
        this.merchantTimeZone = "+8:00";
        this.b2sTxnEndURL = b2sTxnEndURL;
        this.b2sTxnEndURLParam = "";
        this.s2sTxnEndURL = s2sTxnEndURL;
        this.s2sTxnEndURLParam = "";
        this.clientType = "W";
        this.supMsg = "";
        this.netsMidIndicator = "U";
        this.ipAddress = "192.168.124.64";//119.23.136.80
        this.language = "en";
    }
}
