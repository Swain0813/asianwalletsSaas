package com.asianwallets.trade.channels.help2pay;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.Help2PayCallbackDTO;

import javax.servlet.http.HttpServletResponse;


public interface Help2PayService {


    /**
     * help2Pay网银收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse onlinePay(Orders orders, Channel channel);


    /**
     * help2Pay浏览器回调方法
     *
     * @param help2PayCallbackDTO help2Pay回调实体
     * @return
     */
    void help2PayBrowserCallback(Help2PayCallbackDTO help2PayCallbackDTO, HttpServletResponse response);

    /**
     * help2Pay服务器回调方法
     *
     * @param help2PayCallbackDTO help2Pay回调实体
     * @return
     */
    void help2PayServerCallback(Help2PayCallbackDTO help2PayCallbackDTO, HttpServletResponse response);

}
