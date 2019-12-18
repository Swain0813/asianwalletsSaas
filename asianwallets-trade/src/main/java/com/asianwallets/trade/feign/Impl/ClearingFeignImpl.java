package com.asianwallets.trade.feign.Impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.CSFrozenFundsRequest;
import com.asianwallets.common.vo.clearing.IntoAndOutMerhtAccountRequest;
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
    public IntoAndOutMerhtAccountRequest intoAndOutMerhtAccount(IntoAndOutMerhtAccountRequest intoAndOutMerhtAccountRequest) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public CSFrozenFundsRequest CSFrozenFunds(CSFrozenFundsRequest ffr) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
