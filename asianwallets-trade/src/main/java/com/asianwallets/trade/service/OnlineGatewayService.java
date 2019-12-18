package com.asianwallets.trade.service;

import com.asianwallets.common.vo.OnlineTradeVO;
import com.asianwallets.trade.dto.OnlineTradeDTO;

/**
 * 亚洲钱包业务
 */
public interface OnlineGatewayService {

    /**
     * 网关收单
     *
     * @param onlineTradeDTO 线上收单输入实体
     * @return OnlineTradeVO 线上收单输出实体
     */
    OnlineTradeVO gateway(OnlineTradeDTO onlineTradeDTO);

    /* *//**
     * 收银台所需的信息
     *
     * @param orderId
     * @param language
     * @return
     *//*
    BaseResponse cashier(String orderId, String language);

    *//**
     * 线上通道订单状态查询
     *
     * @param onlineOrderQueryDTO
     * @return
     *//*
    BaseResponse onlineOrderQuery(OnlineOrderQueryDTO onlineOrderQueryDTO);

    *//**
     * 收银台收单
     *
     * @param cashierDTO
     * @return
     *//*
    BaseResponse cashierGateway(CashierDTO cashierDTO);

  *//*  *//**//**
     * 查询线上订单信息
     *
     * @param onlineqOrderInfoDTO
     * @return
     *//**//*
    List<OnlineOrdersInfoVO> pageOnlineqOrderInfo(OnlineqOrderInfoDTO onlineqOrderInfoDTO);

    *//**//**
     * 线上通道订单状态查询 RSA
     *
     * @param onlineOrderQueryRSADTO
     * @return
     *//**//*
    BaseResponse onlineqOrderQueryingUseRSA(OnlineOrderQueryRSADTO onlineOrderQueryRSADTO);*//*

     *//**
     * 模拟界面用
     *
     * @param placeOrdersDTO
     * @return
     *//*
    BaseResponse imitateGateway(PlaceOrdersDTO placeOrdersDTO);*/
}
