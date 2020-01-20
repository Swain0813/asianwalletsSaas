package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportTradeAccountVO;
import com.asianwallets.permissions.feign.base.TradeCheckAccountFeign;
import org.springframework.stereotype.Component;

@Component
public class TradeCheckAccountFeignImpl implements TradeCheckAccountFeign {

    @Override
    public BaseResponse pageFindTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindTradeCheckAccountDetail(TradeCheckAccountDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public ExportTradeAccountVO exportTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
