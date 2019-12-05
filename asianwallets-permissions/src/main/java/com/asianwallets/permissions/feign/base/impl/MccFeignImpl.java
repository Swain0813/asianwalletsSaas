package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.MccFeign;
import org.springframework.stereotype.Component;

/**
 * mcc
 */
@Component
public class MccFeignImpl implements MccFeign {
    @Override
    public BaseResponse inquireAllMcc() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
