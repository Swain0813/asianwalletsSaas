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
    BaseResponse doRefundOrder(OrderRefund orderRefund, Channel channel);


    /**
     * @Author YangXu
     * @Date 2019/12/24
     * @Descripate 人工退款接口
     * @return
     **/
    BaseResponse artificialRefund(String username, String refundOrderId, Boolean enabled, String remark);
}
