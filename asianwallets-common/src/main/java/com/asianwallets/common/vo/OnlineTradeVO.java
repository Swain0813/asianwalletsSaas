package com.asianwallets.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "线上收单接口输出实体", description = "线上收单接口输出实体")
public class OnlineTradeVO {
    @ApiModelProperty(value = "签名")//1中文 2英文
    @JsonIgnore
    private String sign;

    @ApiModelProperty(value = "返回结果")//T000表示成功，其余见响应码列表
    private String respCode;

    @ApiModelProperty(value = "返回结果描述")//success,成功或者错误提示
    @JsonIgnore
    private String respMsg;

    @ApiModelProperty(value = "线上返回的付款url")//线上返回的付款url
    private String code_url;

    @ApiModelProperty(value = "类型")//WECHAT(微信) ALIPAY(支付宝) NETBANK(网银) ITS
    private String type;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeAmount;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;
}
