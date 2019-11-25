package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import org.springframework.stereotype.Service;

@Service
public class InstitutionFeignImpl implements InstitutionFeign {

    @Override
    public BaseResponse getInstitutionInfoById(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
