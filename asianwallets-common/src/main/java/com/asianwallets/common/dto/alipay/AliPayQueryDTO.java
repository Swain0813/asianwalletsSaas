package com.asianwallets.common.dto.alipay;

import com.asianwallets.common.entity.Channel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 支付宝查询请求实体
 * @author: XuWenQi
 * @create: 2019-06-28 10:10
 **/
@Data
@ApiModel(value = "支付宝查询请求实体", description = "支付宝查询请求实体")
public class AliPayQueryDTO {

    @ApiModelProperty(value = "接口名称")
    private String service;

    @ApiModelProperty(value = "编码格式")
    private String _input_charset;

    @ApiModelProperty(value = "签约的支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成")
    private String partner;

    @ApiModelProperty(value = "查询订单号")
    private String partner_trans_id;

    @ApiModelProperty(value = "md5KeyStr")
    private String md5KeyStr;

    @ApiModelProperty(value = "channel")
    private Channel channel;

    public AliPayQueryDTO() {
    }

    public AliPayQueryDTO(String id, Channel channel) {
        this.service = "alipay.acquire.overseas.query";
        this._input_charset = "UTF-8";
        this.partner = channel.getChannelMerchantId();
        this.partner_trans_id = id;
        this.md5KeyStr = channel.getMd5KeyStr();
        this.channel = channel;
    }
}
