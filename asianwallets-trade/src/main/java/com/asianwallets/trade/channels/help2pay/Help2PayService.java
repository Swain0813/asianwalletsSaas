package com.asianwallets.trade.channels.help2pay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;


public interface Help2PayService {

    /**
     * 线上收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    BaseResponse onlinePay(Orders orders, Channel channel);

}
