package com.asianwallets.trade.channels.eghl;

import com.asianwallets.common.entity.Channel;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.EghlBrowserCallbackDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * EGHL通道业务接口
 */
public interface EGHLService {

    /**
     * EGHL收单方法
     *
     * @param orders  订单
     * @param channel 通道
     * @return
     */
    BaseResponse onlinePay(Orders orders, Channel channel);

    /**
     * EGHL回调浏览器方法
     *
     * @param eghlBrowserCallbackDTO eghl回调输入实体
     * @return
     */
    void eghlBrowserCallback(EghlBrowserCallbackDTO eghlBrowserCallbackDTO, HttpServletResponse response);

    /**
     * EGHL服务器回调方法
     *
     * @param eghlBrowserCallbackDTO eghl回调输入实体
     * @return
     */
    void eghlServerCallback(EghlBrowserCallbackDTO eghlBrowserCallbackDTO, HttpServletResponse response);
}
