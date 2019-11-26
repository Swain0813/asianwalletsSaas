package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.CountryFeign;
import org.springframework.stereotype.Component;

/**
 * @ClassName CountryFeignImpl
 * @Description 国家
 * @Author abc
 * @Date 2019/11/26 12:01
 * @Version 1.0
 */
@Component
public class CountryFeignImpl implements CountryFeign {
    @Override
    public BaseResponse addCountry(CountryDTO country) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateCountry(CountryDTO country) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageCountry(CountryDTO country) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banCountry(CountryDTO country) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse inquireAllCountry() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
