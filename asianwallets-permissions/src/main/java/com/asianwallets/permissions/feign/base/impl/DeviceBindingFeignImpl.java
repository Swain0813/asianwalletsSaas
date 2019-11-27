package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.DeviceBindingDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.DeviceBindingFeign;
import org.springframework.stereotype.Component;

/**
 * 设备绑定Feign短路器
 */
@Component
public class DeviceBindingFeignImpl implements DeviceBindingFeign {
    @Override
    public BaseResponse addDeviceBinding(DeviceBindingDTO deviceBindingDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse banDeviceBinding(DeviceBindingDTO deviceBindingDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageDeviceBinding(DeviceBindingDTO deviceBindingDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
