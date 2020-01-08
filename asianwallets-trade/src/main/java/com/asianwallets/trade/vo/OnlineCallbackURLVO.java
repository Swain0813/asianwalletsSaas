package com.asianwallets.trade.vo;

import cn.hutool.core.date.DateUtil;
import com.asianwallets.common.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shenxinran
 * @Date: 2019/3/15 14:08
 * @Description: 商户回调信息实体
 */
@Data
@ApiModel(value = "回调信息输出URL实体", description = "回调信息URL实体")
public class OnlineCallbackURLVO {

    @ApiModelProperty(value = "回调实体")
    private OnlineCallbackVO onlineCallbackVO;

    @ApiModelProperty(value = "商户的回调地址")
    private String returnUrl;

    public OnlineCallbackURLVO(Orders orders) {
        OnlineCallbackVO ocv = new OnlineCallbackVO();
        ocv.setReferenceNo(orders.getId());
        ocv.setMerchantId(orders.getMerchantId());
        ocv.setOrderNo(orders.getMerchantOrderId());
        ocv.setOrderTime(DateUtil.format(orders.getMerchantOrderTime(), "yyyyMMddHHmmss"));
        ocv.setTxnTime(DateUtil.format(orders.getChannelCallbackTime(), "yyyyMMddHHmmss"));
        ocv.setOrderCurrency(orders.getOrderCurrency());
        ocv.setOrderAmount(orders.getOrderAmount());
        ocv.setFee(orders.getFee());
        ocv.setTxnStatus(orders.getTradeStatus());
        this.onlineCallbackVO = ocv;
        this.returnUrl = orders.getServerUrl();
    }

    public OnlineCallbackURLVO() {
    }
}
