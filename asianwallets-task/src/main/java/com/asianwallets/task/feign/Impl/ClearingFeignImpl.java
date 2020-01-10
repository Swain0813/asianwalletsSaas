package com.asianwallets.task.feign.Impl;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.task.feign.ClearingFeign;
import org.springframework.stereotype.Service;

/**
 * 清结算feing的实现类
 */
@Service
public class ClearingFeignImpl implements ClearingFeign {

    /**
     * 资金冻结/解冻接口
     * @param ffr
     * @return
     */
    @Override
    public FinancialFreezeDTO CSFrozenFunds(FinancialFreezeDTO ffr) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
