package com.asianwallets.task.service;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;

/**
 * 清结算相关接口
 */
public interface ClearingService {


    /**
     * 资金冻结接口
     *
     * @return
     */
    BaseResponse freezingFunds(FinancialFreezeDTO financialFreezeDTO);
}
