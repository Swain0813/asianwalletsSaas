package com.asianwallets.trade.channels.nextpos;

import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;

public interface NextPosService {



    /**
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     * @return
     **/
    BaseResponse refund(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);
    /**
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 撤销接口
     * @return
     **/
    BaseResponse cancel(Channel channel,OrderRefund orderRefund, RabbitMassage rabbitMassage);


    /**
     * @Author YangXu
     * @Date 2019/12/23
     * @Descripate 退款不上报清结算
     * @return
     **/
    BaseResponse cancelPaying(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);
}
