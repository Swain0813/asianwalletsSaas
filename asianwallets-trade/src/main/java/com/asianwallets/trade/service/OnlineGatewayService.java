package com.asianwallets.trade.service;

import com.asianwallets.common.dto.CashierDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.CalcRateDTO;
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
    BaseResponse gateway(OnlineTradeDTO onlineTradeDTO);

    /**
     * 收银台基础信息
     *
     * @param orderId  订单ID
     * @param language 语言
     * @return BaseResponse
     */
    BaseResponse cashier(String orderId, String language);

    /**
     * 收银台换汇金额计算
     *
     * @param calcRateDTO 订单输入实体
     * @return 换汇计算输出实体
     */
    BaseResponse calcCashierExchangeRate(CalcRateDTO calcRateDTO);

    /**
     * 收银台收单
     *
     * @param cashierDTO 收银台收单实体
     * @return BaseResponse
     */
    BaseResponse cashierGateway(CashierDTO cashierDTO);
}
