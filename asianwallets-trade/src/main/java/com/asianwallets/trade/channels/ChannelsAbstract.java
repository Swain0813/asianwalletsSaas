package com.asianwallets.trade.channels;
import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.dto.th.ISO8583.ISO8583DTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.entity.PreOrders;
import com.asianwallets.common.response.BaseResponse;
import org.springframework.stereotype.Component;


/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-18 10:29
 **/
@Component
public abstract class ChannelsAbstract {

    /**
     * 线下CSB处理方法
     *
     * @param orders       订单
     * @param channel      通道
     */
    public abstract BaseResponse offlineCSB(Orders orders, Channel channel);

    /**
     * 线下BSC处理方法
     *
     * @param orders       订单
     * @param channel      通道
     * @param authCode     支付条码
     * @return 通用响应实体
     */
    public abstract BaseResponse offlineBSC(Orders orders, Channel channel, String authCode);


    /**
     * 线下银行卡下单
     *
     * @param orders       订单
     * @param channel      通道
     */
    public abstract BaseResponse bankCardReceipt(Orders orders, Channel channel);

    /**
     * 线上收单方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    public abstract BaseResponse onlinePay(Orders orders, Channel channel);


    /**
     * 退款方法
     * @return 通用响应实体
     */
    public abstract BaseResponse refund( Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * 撤销方法
     *
     * @param channel      通道
     * @return 通用响应实体
     */
    public abstract BaseResponse cancel(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * 冲正方法
     *
     * @param channel      冲正
     * @return 通用响应实体
     */
    public abstract BaseResponse reversal(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * 银行卡退款
     *
     * @param channel
     * @return 通用响应实体
     */
    public abstract BaseResponse bankRefund(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);


    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 付款中撤销
     **/
    public abstract BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * @param iso8583DTO
     * @return
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 通化签到
     */
    public abstract BaseResponse thSign(ISO8583DTO iso8583DTO);


    /**
     * 预授权
     *
     * @param preOrders
     * @param channel
     * @return
     */
    public abstract BaseResponse preAuth(PreOrders preOrders, Channel channel);

    /**
     * 预授权冲正
     * @param channel
     * @param preOrders
     * @param rabbitMassage
     * @return
     */
    public abstract BaseResponse preAuthReverse(Channel channel,PreOrders preOrders,RabbitMassage rabbitMassage);

    /**
     * 预授权撤销
     * @param channel
     * @param preOrders
     * @param rabbitMassage
     * @return
     */
    public abstract BaseResponse preAuthRevoke(Channel channel,PreOrders preOrders,RabbitMassage rabbitMassage);

    /**
     * 预授权完成
     * @param orders
     * @param channel
     * @return
     */
    public abstract BaseResponse preAuthComplete(Orders orders, Channel channel);

    /**
     * 预授权完成撤销
     * @param channel
     * @param orderRefund
     * @param rabbitMassage
     * @return
     */
    public abstract BaseResponse preAuthCompleteRevoke(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);
}
