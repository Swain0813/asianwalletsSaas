package com.asianwallets.base.service;

import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.DeviceBindingDTO;
import com.asianwallets.common.entity.DeviceBinding;
import com.asianwallets.common.vo.DeviceBindingVO;
import com.github.pagehelper.PageInfo;

/**
 * @author shenxinran
 * @Date: 2019/3/8 13:53
 * @Description: 设备绑定Service
 */
public interface DeviceBindingService extends BaseService<DeviceBinding> {

    /**
     * 新增设备绑定信息
     *
     * @param deviceBindingDTO
     * @return
     */
    int addDeviceBinding(DeviceBindingDTO deviceBindingDTO);

    /**
     * 解绑设备
     *
     * @param deviceBindingDTO
     * @return
     */
    int banDeviceBinding(DeviceBindingDTO deviceBindingDTO);

    /**
     * 查询设备绑定信息
     *
     * @param deviceBindingDTO
     * @return
     */
    PageInfo<DeviceBindingVO> pageDeviceBinding(DeviceBindingDTO deviceBindingDTO);
}
