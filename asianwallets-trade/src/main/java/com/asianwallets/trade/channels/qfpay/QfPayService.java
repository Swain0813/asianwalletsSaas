package com.asianwallets.trade.channels.qfpay;


import com.asianwallets.trade.dto.QfPayCallbackDTO;

public interface QfPayService {

    /**
     * qfPay服务器回调
     *
     * @param qfPayCallbackDTO QfPay回调实体
     * @return
     */
    String qfPayServerCallback(QfPayCallbackDTO qfPayCallbackDTO);
}
