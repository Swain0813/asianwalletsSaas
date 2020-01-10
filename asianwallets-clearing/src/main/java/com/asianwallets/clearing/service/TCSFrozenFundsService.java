package com.asianwallets.clearing.service;

import com.asianwallets.common.entity.TcsFrozenFundsLogs;
import com.asianwallets.common.response.BaseResponse;

public interface TCSFrozenFundsService {

    /**
     * 资金冻结和解冻
     * @param ffl
     * @return
     */
    BaseResponse frozenFundsLogs(TcsFrozenFundsLogs ffl);
}
