package com.asianwallets.common.dto.wechat;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.utils.UUIDHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @description: 微信线下BSC请求实体
 * @author: XuWenQi
 * @create: 2019-06-25 10:11
 **/
@Data
@ApiModel(value = "微信线下BSC请求实体", description = "微信线下BSC请求实体")
public class WechatBSCDTO {

    @ApiModelProperty(value = "公众号id")
    private String appid;

    @ApiModelProperty(value = "随机字符串")
    private String nonce_str;

    @ApiModelProperty(value = "商户号")
    private String mch_id;

    @ApiModelProperty(value = "子商户号")
    private String sub_mch_id;

    @ApiModelProperty(value = "签名类型")
    private String sign_type;

    @ApiModelProperty(value = "商品描述")
    private String body;

    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;

    @ApiModelProperty(value = "标价币种")
    private String fee_type;

    @ApiModelProperty(value = "标价金额")
    private String total_fee;

    @ApiModelProperty(value = "终端IP")
    private String spbill_create_ip;

    @ApiModelProperty(value = "付款码")
    private String auth_code;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "新增mcc")
    private String detail;

    //以下不是上报通道的参数
    @ApiModelProperty(value = "机构订单号")
    private String institutionOrderId;

    @ApiModelProperty(value = "md5Key")
    private String md5KeyStr;

    @ApiModelProperty(value = "ip")
    private String reqIp;

    @ApiModelProperty(value = "Channel")
    private Channel channel;

    public WechatBSCDTO() {
    }

    public WechatBSCDTO(Orders orders, Channel channel, String auth_code) {
        this.appid = "wx14e049b9320bccca";
        this.nonce_str = UUIDHelper.getRandomString(32);
        this.mch_id = channel.getChannelMerchantId();
        this.sub_mch_id = "66104046";
        this.sign_type = "MD5";
        this.body = StringUtils.isEmpty(orders.getProductName()) ? "商品" : orders.getProductName();//产品名称
        this.out_trade_no = orders.getId();
        this.fee_type = orders.getTradeCurrency();
        String amt = String.valueOf(orders.getTradeAmount());
        int total_fee = 0;
        if (!StringUtils.isEmpty(amt)) {
            Double amt_d = new Double(amt);
            total_fee = BigDecimal.valueOf(amt_d).multiply(new BigDecimal(100)).intValue();
        }
        this.total_fee = Integer.toString(total_fee);
        this.spbill_create_ip = "8.8.8.8";
        this.auth_code = auth_code;
        this.version = "1.0";
        String mccCode = "";
        this.detail = "{\"goods_detail\":[{\"wxpay_goods_id\":\"" + mccCode + "\"}]}";
        this.institutionOrderId = orders.getMerchantOrderId();
        this.md5KeyStr = channel.getMd5KeyStr();
        this.reqIp = orders.getReqIp();
        this.channel = channel;
    }
}
