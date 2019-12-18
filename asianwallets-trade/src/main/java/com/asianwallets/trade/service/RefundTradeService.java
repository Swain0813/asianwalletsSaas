package com.asianwallets.trade.service;

import com.asianwallets.common.dto.RefundDTO;
import com.asianwallets.common.response.BaseResponse;

public interface RefundTradeService {


    /**
     * @Author YangXu
     * @Date 2019/12/18
     * @Descripate 退款撤销接口
     * @return
     **/
    BaseResponse refundOrder(RefundDTO refundDTO, String reqIp);
}
