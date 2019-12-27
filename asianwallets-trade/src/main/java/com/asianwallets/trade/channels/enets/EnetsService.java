package com.asianwallets.trade.channels.enets;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.EnetsCallbackDTO;
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


    /**
     * enets网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse eNetsBankPay(Orders orders, Channel channel);

    /**
     * enets线上扫码收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse eNetsOnlineQRCode(Orders orders, Channel channel);

    /**
     * enets网银浏览器回调方法
     *
     * @param enetsCallbackDTO enets回调实体
     * @param response
     * @return
     */
    void eNetsBankBrowserCallback(EnetsCallbackDTO enetsCallbackDTO, String txnRes, HttpServletResponse response);

    /**
     * enets网银服务器回调方法
     *
     * @param enetsCallbackDTO enets回调实体
     * @return
     */
    ResponseEntity<Void> eNetsBankServerCallback(EnetsCallbackDTO enetsCallbackDTO, String txnRes, HttpServletResponse response);


    /**
     * enets线上扫码浏览器回调方法
     *
     * @param enetsCallbackDTO enets回调实体
     * @return
     */
    void eNetsQrCodeBrowserCallback(EnetsCallbackDTO enetsCallbackDTO, String txnRes, HttpServletResponse response);

    /**
     * enets线上扫码服务器回调方法
     *
     * @param enetsCallbackDTO enets回调实体
     * @return
     */
    ResponseEntity<Void> eNetsQrCodeServerCallback(EnetsCallbackDTO enetsCallbackDTO, String txnRes, HttpServletResponse response);
}
