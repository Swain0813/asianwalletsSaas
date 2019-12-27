package com.asianwallets.trade.channels.enets;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.EnetsPosCallbackDTO;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

public interface EnetsService {

    /**
     * Enets线下CSB
     *
     * @param orders  订单
     * @param channel 通道
     * @return BaseResponse
     */
    BaseResponse offlineCSB(Orders orders, Channel channel);

    /**
     * EnetsCSB回调
     *
     * @param enetsPosCallbackDTO eNetsCsb回调实体
     * @return
     */
    ResponseEntity<Void> eNetsCsbCallback(EnetsPosCallbackDTO enetsPosCallbackDTO, HttpServletResponse response);
}
