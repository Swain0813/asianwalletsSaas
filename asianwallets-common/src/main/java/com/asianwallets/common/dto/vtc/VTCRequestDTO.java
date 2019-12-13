package com.asianwallets.common.dto.vtc;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: VTC通道请求实体
 * @author: YangXu
 * @create: 2019-05-30 17:07
 **/
@Data
@ApiModel(value = "VTC通道请求实体", description = "VTC通道请求实体")
public class VTCRequestDTO {

    @ApiModelProperty(value = "网站id")
    private String website_id;

    @ApiModelProperty(value = "订单金额")
    private String amount;

    @ApiModelProperty(value = "通道商户号")
    private String receiver_account;

    @ApiModelProperty(value = "订单号")
    private String reference_number;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "回调地址")
    private String url_return;

    @ApiModelProperty(value = "银行机构号")
    private String payment_type;

    @ApiModelProperty(value = "签名")
    private String signature;

    //以下不是上报VTC参数
    @ApiModelProperty(value = "机构订单号")
    private String institutionOrderId;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "md5key")
    private String md5KeyStr;

    @ApiModelProperty(value = "Channel")
    private Channel channel;


    public VTCRequestDTO() {
    }

    public VTCRequestDTO(Orders orders, Channel channel, String retURL) {
        this.website_id = "876";//网站id
        this.amount = String.valueOf(orders.getTradeAmount());//订单金额
        this.receiver_account = channel.getChannelMerchantId();
        this.reference_number = orders.getId();//订单号
        this.currency = orders.getTradeCurrency();//币种
        this.url_return = retURL;
        this.payment_type = orders.getIssuerId();
        this.signature = "";
        this.reqIp = orders.getReqIp();
        this.md5KeyStr = channel.getMd5KeyStr();
        this.institutionOrderId = orders.getMerchantOrderId();
        this.channel = channel;
    }
}
