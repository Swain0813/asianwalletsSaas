package com.asianwallets.base.service;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;

public interface ClearingService {

    /**
     * 资金变动接口
     * 场景支付成功后上报清结算系统
     *
     * @return
     */
    BaseResponse fundChange(FundChangeDTO fundChangeDTO);

    /**
     * 资金冻结接口
     *
     * @return
     */
    BaseResponse freezingFunds(FinancialFreezeDTO financialFreezeDTO);
}
