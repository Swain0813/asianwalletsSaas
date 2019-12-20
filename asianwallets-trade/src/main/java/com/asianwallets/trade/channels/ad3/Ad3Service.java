package com.asianwallets.trade.channels.ad3;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

public interface Ad3Service {

    /**
     * AD3线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse offlineCSB(Orders orders, Channel channel);
}
