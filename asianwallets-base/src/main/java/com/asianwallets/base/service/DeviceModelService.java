package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.entity.DeviceModel;
import com.asianwallets.common.vo.DeviceModelVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 设备型号管理接口
 */
public interface DeviceModelService extends BaseService<DeviceModel> {

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
