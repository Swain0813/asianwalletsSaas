package com.asianwallets.trade.service;

import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.OrderRefund;
import com.asianwallets.common.response.BaseResponse;

public interface RefundTradeService {


    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
     * @return
     **/
    BaseResponse refundOrder(RefundDTO refundDTO, String reqIp);

    /**
     * @return
     * @Author YangXu
     * @Date 2019/3/14
     * @Descripate 退款操作
     **/
     void doRefundOrder(OrderRefund orderRefund, Channel channel);
}
