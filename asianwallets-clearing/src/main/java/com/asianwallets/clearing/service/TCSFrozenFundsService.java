package com.asianwallets.clearing.service;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;

public interface TCSFrozenFundsService {

    /**
     * 资金冻结和解冻
     * @param ffl
     * @return
     */
    BaseResponse frozenFundsLogs(FinancialFreezeDTO ffl);
}
