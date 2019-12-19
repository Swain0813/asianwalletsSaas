package com.asianwallets.trade.channels;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import org.springframework.stereotype.Component;

/**
 * 处理器适配器类,空实现,具体实现继承该类
 */
@Component
public class ChannelsAbstractAdapter extends ChannelsAbstract {

    @Override
    public BaseResponse offlineCSB(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode) {
        return null;
    }

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse refund( Channel channel,OrderRefund orderRefund) {
        return null;
    }

    @Override
    public BaseResponse cancel(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse cancelPaying(Orders orders, Channel channel) {
        return null;
    }
}
