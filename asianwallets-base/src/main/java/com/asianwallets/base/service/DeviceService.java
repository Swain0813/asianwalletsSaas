package com.asianwallets.base.service;

import com.asianwallets.common.dto.DeviceVendorDTO;

/**
 * 设备管理
 */
public interface DeviceService {

    /**
     * 新增厂商
     *
     * @param deviceVendorDTO
     * @return 条数
     */
    int addDeviceVendor(DeviceVendorDTO deviceVendorDTO);

    /**
     * 修改厂商
     *
     * @param deviceVendorDTO
     * @return 影响条数
     */
    int updateDeviceVendor(DeviceVendorDTO deviceVendorDTO);
}
