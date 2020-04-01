package com.asianwallets.channels.service;
import com.asianwallets.common.dto.qfpay.*;
import com.asianwallets.common.response.BaseResponse;

public interface QfPayService {

    /**
     * qfPayCSB
     * @param qfPayDTO
     * @return
     */
    BaseResponse qfPayCSB(QfPayDTO qfPayDTO);

    /**
     * qfPayBSC
     * @param qfPayDTO
     * @return
     */
    BaseResponse qfPayBSC(QfPayDTO qfPayDTO);

    /**
     * qfPayQuery
     * @param qfPayDTO
     * @return
     */
    BaseResponse qfPayQuery(QfPayDTO qfPayDTO);

    /**
     * qfPayRefund
     * @param qfPayDTO
     * @return
     */
    BaseResponse qfPayRefund(QfPayDTO qfPayDTO);

    /**
     * @Author YangXu
     * @Date 2020/2/18
     * @Descripate qfPayRefundSearch
     * @return
     **/
    BaseResponse qfPayRefundSearch(QfPayDTO qfPayDTO);
}
