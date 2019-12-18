package com.asianwallets.trade.channels;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
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
     * 线上收单方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    public abstract BaseResponse onlinePay(Orders orders, Channel channel);


    /**
     * 退款方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    public abstract BaseResponse refund(Orders orders, Channel channel);
    /**
     * 撤销方法
     *
     * @param orders       订单
     * @param channel      通道
     * @return 通用响应实体
     */
    public abstract BaseResponse cancle(Orders orders, Channel channel);


    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate  付款中撤销
     * @return
     **/
    public abstract BaseResponse cancelPaying(Orders orders, Channel channel);




}
