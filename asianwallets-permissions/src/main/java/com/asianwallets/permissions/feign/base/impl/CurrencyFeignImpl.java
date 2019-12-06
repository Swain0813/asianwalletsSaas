package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.CurrencyExportVO;
import com.asianwallets.permissions.feign.base.CurrencyFeign;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName CurrencyFeignImpl
 * @Description TODO
 * @Author abc
 * @Date 2019/11/26 12:07
 * @Version 1.0
 */
@Component
public class CurrencyFeignImpl implements CurrencyFeign {

    @Override
    public BaseResponse addCurrency(CurrencyDTO currencyDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateCurrency(CurrencyDTO currencyDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageCurrency(CurrencyDTO currencyDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banCurrency(CurrencyDTO currencyDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse inquireAllCurrency() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }


    @Override
    public List<CurrencyExportVO> exportCurrency(CurrencyDTO currencyDTO){
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
