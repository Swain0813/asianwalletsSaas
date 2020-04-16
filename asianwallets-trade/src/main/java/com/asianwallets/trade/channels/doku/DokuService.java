package com.asianwallets.trade.channels.doku;

import com.asianwallets.trade.dto.DokuBrowserCallbackDTO;
import com.asianwallets.trade.dto.DokuServerCallbackDTO;

import javax.servlet.http.HttpServletResponse;

public interface DokuService {

    /***
     * doku服务器回调
     * @param dokuServerCallbackDTO doku服务器回调实体
     * @return
     */
    void dokuServerCallback(DokuServerCallbackDTO dokuServerCallbackDTO);

    /***
     * doku浏览器回调
     * @param dokuBrowserCallbackDTO doku服务器回调实体
     * @return
     */
    void dokuBrowserCallback(DokuBrowserCallbackDTO dokuBrowserCallbackDTO, HttpServletResponse response);

}
