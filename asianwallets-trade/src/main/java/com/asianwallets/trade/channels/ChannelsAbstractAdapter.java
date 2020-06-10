package com.asianwallets.trade.channels;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.PreOrders;
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
    public BaseResponse bankCardReceipt(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse onlinePay(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse refund( Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse cancel(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse reversal(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse bankRefund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage){
        return null;
    }

    @Override
    public BaseResponse preAuth(PreOrders preOrders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse preAuthReverse(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse preAuthRevoke(Channel channel, PreOrders preOrders, RabbitMassage rabbitMassage) {
        return null;
    }

    @Override
    public BaseResponse preAuthComplete(Orders orders, Channel channel) {
        return null;
    }

    @Override
    public BaseResponse preAuthCompleteRevoke(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage) {
        return null;
    }


}
