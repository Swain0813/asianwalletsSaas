package com.asianwallets.trade.channels.nganluong;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

public interface NganLuongService {

    /**
     * nganLuong网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse nganLuongPay(Orders orders, Channel channel, BaseResponse baseResponse);
}
