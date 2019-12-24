package com.asianwallets.trade.vo;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author shenxinran
 * @Date: 2019/3/15 14:08
 * @Description: 商户回调信息实体
 */
@Data
@ApiModel(value = "回调信息输出实体", description = "回调信息实体")
public class OnlineCallbackVO {

    @ApiModelProperty(value = "系统流水号")
    private String referenceNo;

    @ApiModelProperty(value = "商户编号")
    private String institutionId;

    @ApiModelProperty(value = "商户上送的商户订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户上送订单时间")
    private String orderTime;

    @ApiModelProperty(value = "订单币种")
    private String orderCurrency;

    @ApiModelProperty(value = "交易时间")
    private String txnTime;

    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "订单状态")
    private Byte txnStatus;

    public OnlineCallbackVO() {
    }

    public OnlineCallbackVO(Orders orders) {
        this.referenceNo = orders.getId();
        this.institutionId = orders.getMerchantId();//商户编号
        this.orderNo = orders.getMerchantOrderId();//商户订单号
        this.orderTime = DateUtil.format(orders.getMerchantOrderTime(), "yyyyMMddHHmmss");
        this.txnTime = DateUtil.format(orders.getChannelCallbackTime(), "yyyyMMddHHmmss");
        this.orderCurrency = orders.getOrderCurrency();
        this.orderAmount = orders.getOrderAmount();
        this.fee = orders.getFee();
        this.txnStatus = orders.getTradeStatus();
    }

}
