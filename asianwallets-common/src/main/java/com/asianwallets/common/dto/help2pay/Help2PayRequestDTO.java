package com.asianwallets.common.dto.help2pay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.DateToolUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author: XuWenQi
 * @create: 2019-06-10 10:20
 **/

@Data
@ApiModel(value = "Help2Pay通道请求实体", description = "Help2Pay通道请求实体")
public class Help2PayRequestDTO {

    @ApiModelProperty(value = "商户号")
    private String Merchant;

    @ApiModelProperty(value = "币种")
    private String Currency;

    @ApiModelProperty(value = "customer")
    private String Customer;

    @ApiModelProperty(value = "系统流水号")
    private String Reference;

    @ApiModelProperty(value = "订单金额")
    private String Amount;

    @ApiModelProperty(value = "备注")
    private String Note;

    @ApiModelProperty(value = "订单时间")
    private String Datetime;

    @ApiModelProperty(value = "浏览器回调")
    private String FrontURI;

    @ApiModelProperty(value = "服务器回调")
    private String BackURI;

    @ApiModelProperty(value = "语言")
    private String Language;

    @ApiModelProperty(value = "银行")
    private String Bank;

    @ApiModelProperty(value = "签名")
    private String Key;

    @ApiModelProperty(value = "ip")
    private String ClientIP;


    @ApiModelProperty(value = "机构订单号")
    private String institutionOrderId;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "请求ip")
    private String reqIp;

    @ApiModelProperty(value = "请求ip")
    private String payUrl;

    @ApiModelProperty(value = "Channel")
    private Channel channel;


    public Help2PayRequestDTO() {
    }

    public Help2PayRequestDTO(Orders orders, Channel channel, String FrontURI, String BackURI, String md5KeyStr) {
        this.Merchant = channel.getChannelMerchantId();
        this.Currency = orders.getTradeCurrency();
        this.Customer = orders.getInstitutionCode();
        this.Reference = orders.getId();
        //金额必须格式化
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        BigDecimal amt = orders.getTradeAmount();//交易金额
        Amount = decimalFormat.format(amt);
        Note = orders.getRemark();
        this.FrontURI = FrontURI;
        this.BackURI = BackURI;
        this.Language = "en-us";
        this.Datetime = DateToolUtils.getReqDateyyyyMMddHHmmss(orders.getCreateTime());
        this.Bank = orders.getIssuerId();
        this.md5KeyStr = md5KeyStr;
        this.institutionOrderId = orders.getInstitutionOrderId();
        this.channel = channel;
    }

    public static void main(String[] args) {
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置
        String format = decimalFormat.format(new BigDecimal("1.139"));
        System.out.println(format);
    }
}
