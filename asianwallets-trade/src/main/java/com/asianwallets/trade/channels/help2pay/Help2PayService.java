package com.asianwallets.trade.channels.help2pay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;


public interface Help2PayService {

    /**
     * 线下CSB处理方法
     *
     * @param orders       订单
     * @param channel      通道
     */
    /**
     * 线下CSB处理方法
     *
     * @param orders       订单
     * @param channel      通道
     */
    public BaseResponse offlineCSB(Orders orders, Channel channel);
    /**
     * 线下BSC处理方法
     *
     * @param orders   订单
     * @param channel  通道
     * @param authCode 支付条码
     * @return 通用响应实体
     */
    public BaseResponse offlineBSC(Orders orders, Channel channel, String authCode);


    /**
     * 线上收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    public BaseResponse onlinePay(Orders orders, Channel channel);


    /**
     * 退款方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    public BaseResponse refund(Orders orders, Channel channel);

    /**
     * 撤销方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return 通用响应实体
     */
    public BaseResponse cancle(Orders orders, Channel channel);

    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate  付款中撤销
     * @return
     **/
    public   BaseResponse cancelPaying(Orders orders, Channel channel);



}
