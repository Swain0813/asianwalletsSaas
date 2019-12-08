package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.PayTypeFeign;
import org.springframework.stereotype.Component;

@Component
public class PayTypeFeignImpl implements PayTypeFeign {
    @Override
    public BaseResponse addPaytype(PayTypeDTO PayTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updatePaytype(PayTypeDTO PayTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pagePaytype(PayTypeDTO PayTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banCurrency(PayTypeDTO PayTypeDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse inquireAllPaytype() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
