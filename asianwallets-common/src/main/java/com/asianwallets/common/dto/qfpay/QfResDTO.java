package com.asianwallets.common.dto.qfpay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-02-12 12:32
 **/
@Data
@ApiModel(value = "QfCSBResDTO返回实体", description = "QfCSBResDTO返回实体")
public class QfResDTO {

    @ApiModelProperty(value = "金额")
    public Integer amount;

    @ApiModelProperty(value = "支付渠道类型")
    public String payType;

    @ApiModelProperty(value = "二维码url")
    public String qrcode; //CSB返回字段

    @ApiModelProperty(value = "支付渠道原始单号")
    public String channeltransactionid;// BSC 返回字段

    @ApiModelProperty(value = "原始支付订单号")
    public String orginOrderNum;// 退款返回字段

    @ApiModelProperty(value = "系统交易时间")
    public String sysdtm;

    @ApiModelProperty(value = "退款查询状态码")
    public Integer orderStatus; //支付交易订单的状态：1- 付款中 或 退款中 2- 付款完成 3- 付款 失败(余额不足,或其他原因) 4- 退款 5- 退 款失败。

    @ApiModelProperty(value = "状态码")
    public String status; //支付结果返回码0000 表示交易支付成功1143、1145表示交易中，需继续查询交易结果；其他返回码表示失败

    @ApiModelProperty(value = "通道交易订单号")
    public String orderNum;

    @ApiModelProperty(value = "我们交易订单号")
    public String outTradeNo;


    @ApiModelProperty(value = "实际支付金额")
    public Integer resultCash;

    @ApiModelProperty(value = "支付结果描述")
    public String errorMsg;
}
