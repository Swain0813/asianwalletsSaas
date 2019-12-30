package com.asianwallets.trade.channels.nextpos;

import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface NextPosService {

    /**
     * NextPos线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse offlineCSB(Orders orders, Channel channel);


    /**
     * NextPos线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse onlinePay(Orders orders, Channel channel);

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
     * NextPos回调
     *
     * @param map      回调参数
     * @param response 响应实体
     */
    void nextPosCallback(Map<String, Object> map, HttpServletResponse response);
}
