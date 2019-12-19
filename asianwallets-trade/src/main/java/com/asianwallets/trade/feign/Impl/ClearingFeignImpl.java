package com.asianwallets.trade.feign.Impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import com.asianwallets.trade.feign.ClearingFeign;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-12-18 15:19
 **/
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
