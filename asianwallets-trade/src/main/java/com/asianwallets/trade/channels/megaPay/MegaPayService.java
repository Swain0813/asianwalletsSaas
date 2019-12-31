package com.asianwallets.trade.channels.megaPay;


import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.MegaPayBrowserCallbackDTO;
import com.asianwallets.trade.dto.MegaPayIDRBrowserCallbackDTO;
import com.asianwallets.trade.dto.MegaPayIDRServerCallbackDTO;
import com.asianwallets.trade.dto.MegaPayServerCallbackDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MegaPayService {


    /**
     * MegaPay网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse onlinePay(Orders orders, Channel channel);

    /**
     * MegaPayTHB服务器回调方法
     *
     * @param megaPayServerCallbackDTO megaPayTHB回调参数
     * @return
     */
    void megaPayThbServerCallback(MegaPayServerCallbackDTO megaPayServerCallbackDTO, HttpServletRequest request, HttpServletResponse response);

    /**
     * MegaPayTHB浏览器回调方法
     *
     * @param megaPayCallbackDTO megaPayTHB回调参数
     * @return
     */
    void megaPayThbBrowserCallback(MegaPayBrowserCallbackDTO megaPayCallbackDTO, HttpServletResponse response);

    /**
     * MegaPayIDR服务器回调方法
     *
     * @param megaPayIDRServerCallbackDTO megaPayIDR回调参数
     * @return
     */
    void megaPayIdrServerCallback(MegaPayIDRServerCallbackDTO megaPayIDRServerCallbackDTO, HttpServletRequest request, HttpServletResponse response);

    /**
     * MegaPayIDR浏览器回调方法
     *
     * @param megaPayIDRBrowserCallbackDTO megaPayIDR回调参数
     * @return
     */
    void megaPayIdrBrowserCallback(MegaPayIDRBrowserCallbackDTO megaPayIDRBrowserCallbackDTO, HttpServletResponse response);
}
