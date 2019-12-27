package com.asianwallets.trade.channels.megaPay;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

public interface MegaPayService {


    /**
     * MegaPay网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse onlinePay(Orders orders, Channel channel);
}
