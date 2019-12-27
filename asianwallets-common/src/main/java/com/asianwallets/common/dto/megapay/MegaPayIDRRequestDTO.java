package com.asianwallets.common.dto.megapay;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author: XuWenQi
 * @create: 2019-05-30 15:08
 **/
@Data
@ApiModel(value = "MegaPayIDR通道请求实体", description = "MegaPayIDR通道请求实体")
public class MegaPayIDRRequestDTO {

    @ApiModelProperty(value = "通道商户号")
    private String e_merID;

    @ApiModelProperty(value = "订单id")
    private String e_inv;

    @ApiModelProperty(value = "订单金额")
    private String e_amt;

    @ApiModelProperty(value = "浏览器返回地址")
    private String e_respURL;

    @ApiModelProperty(value = "Client Unique ID")
    private String cusID;

    @ApiModelProperty(value = "用户名")
    private String cusName;

    @ApiModelProperty(value = "银行编码")
    private String bMode;

    //以下不是上报通道参数
    @ApiModelProperty(value = "订单id")
    private String institutionOrderId;

    @ApiModelProperty(value = "交易币种")
    private String md5KeyStr;

    @ApiModelProperty(value = "ip")
    private String reqIp;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public MegaPayIDRRequestDTO() {
    }

    public MegaPayIDRRequestDTO(Orders orders, Channel channel) {
        this.e_merID = channel.getChannelMerchantId();
        this.e_inv = orders.getId();
        //金额转换
        double tempAmount = orders.getChannelAmount().setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat decimalFormat0 = new DecimalFormat("###0");
        String amountStr = decimalFormat0.format(tempAmount);
        this.e_amt = amountStr;
        this.e_respURL = channel.getNotifyServerUrl();
        this.cusID = "thb@alldebit.com";
        this.cusName = "Alldebit";
        this.bMode = channel.getIssuerId();
        this.institutionOrderId = orders.getMerchantOrderId();
        this.md5KeyStr = channel.getMd5KeyStr();
        this.reqIp = orders.getReqIp();
        this.tradeCurrency = orders.getTradeCurrency();
        this.channel = channel;
    }
}
