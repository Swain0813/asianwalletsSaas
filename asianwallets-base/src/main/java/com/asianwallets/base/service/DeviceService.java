package com.asianwallets.base.service;

import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.entity.DeviceVendor;
import com.asianwallets.common.vo.DeviceModelVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 设备管理
 */
public interface DeviceService {

    //-------------------------厂商-------------------------//

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

    /**
     * 查询厂商信息
     *
     * @param deviceVendorDTO
     * @return
     */
    PageInfo<DeviceVendor> pageDeviceVendor(DeviceVendorDTO deviceVendorDTO);

    /**
     * 启用禁用厂商
     *
     * @param deviceVendorDTO
     * @return
     */
    int banDeviceVendor(DeviceVendorDTO deviceVendorDTO);

    //-------------------------型号-------------------------//

    /**
     * 新增设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    int addDeviceModel(DeviceModelDTO deviceModelDTO);

    /**
     * 启用禁用设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    int banDeviceModel(DeviceModelDTO deviceModelDTO);

    /**
     * 修改设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    int updateDeviceModel(DeviceModelDTO deviceModelDTO);

    /**
     * 查询设备型号信息
     *
     * @param deviceModelDTO
     * @return
     */
    PageInfo<DeviceModelVO> pageDeviceModel(DeviceModelDTO deviceModelDTO);

    /**
     * 查询设备型号类别
     *
     * @param
     * @return
     */
    List<DeviceModelVO> queryModelCategory();

}
