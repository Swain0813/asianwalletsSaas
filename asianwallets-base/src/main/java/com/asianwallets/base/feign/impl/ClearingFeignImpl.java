package com.asianwallets.base.feign.impl;

import com.asianwallets.base.feign.ClearingFeign;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import org.springframework.stereotype.Service;

/**
 * 清结算feing的实现类
 */
@Service
public class ClearingFeignImpl implements ClearingFeign {

    @Override
    public FundChangeDTO intoAndOutMerhtAccount(FundChangeDTO fundChangeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public FinancialFreezeDTO CSFrozenFunds(FinancialFreezeDTO ffr) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
