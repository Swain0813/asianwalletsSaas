package com.asianwallets.common.dto.megapay;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: XuWenQi
 * @create: 2019-05-30 15:08
 **/
@Data
@ApiModel(value = "MegaPay通道请求实体", description = "MegaPay通道请求实体")
public class MegaPayRequestDTO {

    @ApiModelProperty(value = "商户id")
    private String merID;

    @ApiModelProperty(value = "订单id")
    private String orderID;

    @ApiModelProperty(value = "订单金额")
    private String amt;

    @ApiModelProperty(value = "银行机构号")
    private String bMode;

    @ApiModelProperty(value = "页面返回地址")
    private String retURL;

    @ApiModelProperty(value = "Client Unique ID")
    private String cusID;

    @ApiModelProperty(value = "用户名")
    private String c_Name;

    //以下不是上报通道参数
    @ApiModelProperty(value = "订单id")
    private String institutionOrderId;

    @ApiModelProperty(value = "订单币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "ip")
    private String reqIp;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public MegaPayRequestDTO() {
    }

    public MegaPayRequestDTO(Orders orders, Channel channel, String retURL) {
        this.merID = channel.getChannelMerchantId();//商户号
        this.orderID = orders.getId();//订单号
        this.amt = String.valueOf(orders.getChannelAmount());//订单金额
        this.bMode = channel.getIssuerId().toLowerCase();//银行机构号,必须小写
        this.retURL = retURL;//页面返回地址
        this.cusID = "";
        this.c_Name = "";
        this.institutionOrderId = orders.getMerchantOrderId();
        this.tradeCurrency = orders.getTradeCurrency();
        this.reqIp = orders.getReqIp();
        this.md5KeyStr = channel.getMd5KeyStr();
        this.channel = channel;
    }
}
