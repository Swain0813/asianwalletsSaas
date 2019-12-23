package com.asianwallets.trade.channels.wechat;

import com.asianwallets.common.dto.RabbitMassage;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.response.BaseResponse;

public interface WechantService {

    /**
     * @Author YangXu
     * @Date 2019/12/19
     * @Descripate 退款接口
     * @return
     **/
    BaseResponse refund(Channel channel, OrderRefund orderRefund, RabbitMassage rabbitMassage);
}
