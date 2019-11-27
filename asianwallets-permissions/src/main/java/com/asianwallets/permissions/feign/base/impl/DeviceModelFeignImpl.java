package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.DeviceModelFeign;
import org.springframework.stereotype.Component;

/**
 * @author shenxinran
 * @Date: 2019/3/6 14:24
 * @Description: 设备型号管理 Feign
 */
@Component
public class DeviceModelFeignImpl implements DeviceModelFeign {
    /**
     * 添加设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public BaseResponse addDeviceModel(DeviceModelDTO deviceModelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 启用警用禁用设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public BaseResponse banDeviceModel(DeviceModelDTO deviceModelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 更新设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public BaseResponse updateDeviceModel(DeviceModelDTO deviceModelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public BaseResponse pageDeviceModel(DeviceModelDTO deviceModelDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    /**
     * 查询设备类别
     *
     * @return
     */
    @Override
    public BaseResponse queryModelCategory() {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
