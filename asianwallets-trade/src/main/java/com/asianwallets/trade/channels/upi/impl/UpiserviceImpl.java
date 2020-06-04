package com.asianwallets.trade.channels.upi.impl;

import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.channels.ChannelsAbstractAdapter;
import com.asianwallets.trade.channels.upi.Upiservice;
import com.asianwallets.trade.utils.HandlerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-06-04 14:40
 **/
@Slf4j
@Service
@HandlerType(TradeConstant.UPI)
public class UpiserviceImpl extends ChannelsAbstractAdapter implements Upiservice {


    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        return null;
    }
}
