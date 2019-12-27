package com.asianwallets.trade.channels.wechat;

import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WechantService {

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     **/
    BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 撤销接口
     **/
    BaseResponse cancel(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     **/
    BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);


    /**
     * 微信线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse offlineCSB(Orders orders, Channel channel);

    /**
     * 微信线下BSC
     *
     * @param orders   订单
     * @param channel  通道
     * @param authCode 付款码
     * @return
     */
    BaseResponse offlineBSC(Orders orders, Channel channel, String authCode);

    /**
     * 微信CSB回调
     *
     * @param request
     * @param response
     */
    void wechatCsbServerCallback(HttpServletRequest request, HttpServletResponse response);
}
