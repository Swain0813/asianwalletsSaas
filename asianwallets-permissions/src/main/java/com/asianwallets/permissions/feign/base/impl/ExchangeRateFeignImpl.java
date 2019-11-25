package com.asianwallets.permissions.feign.base.impl;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.ExchangeRateFeign;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateFeignImpl implements ExchangeRateFeign {


    /**
     * 添加汇率信息
     * @param exchangeRateDTO
     * @return
     */
    @Override
    public BaseResponse addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }


    /**
     * 禁用汇率信息
     * @param id
     * @return
     */
    @Override
    public BaseResponse banExchangeRate(String id ) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 分页查询汇率信息
     * @param exchangeRateDTO
     * @return
     */
    @Override
    public BaseResponse getByMultipleConditions(ExchangeRateDTO exchangeRateDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

}
