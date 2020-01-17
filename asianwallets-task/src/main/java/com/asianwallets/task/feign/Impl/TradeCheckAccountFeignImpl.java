package com.asianwallets.task.feign.Impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.task.feign.TradeCheckAccountFeign;
import org.springframework.stereotype.Component;

@Component
public class TradeCheckAccountFeignImpl implements TradeCheckAccountFeign {

    @Override
    public void tradeCheckAccount() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
