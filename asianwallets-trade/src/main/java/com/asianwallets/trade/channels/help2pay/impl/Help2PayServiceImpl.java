package com.asianwallets.trade.channels.help2pay.impl;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.channels.ChannelsAbstract;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.help2pay.Help2PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Help2PayServiceImpl extends ChannelsAbstractAdapter implements Help2PayService {

    /**
     * 线上收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        System.out.println("--------  onlinePay ---------");
        return null;
    }
}
