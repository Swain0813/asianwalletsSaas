package com.asianwallets.trade.channels.enets;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

/**
 * enets网银
 */
public interface EnetsBankService {


    /**
     * enets网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse onlinePay(Orders orders, Channel channel);


}
