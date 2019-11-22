package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.DeviceBindingDTO;
import com.asianwallets.common.entity.DeviceBinding;
import com.asianwallets.common.vo.DeviceBindingVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备绑定
 */
@Repository
public interface DeviceBindingMapper extends BaseMapper<DeviceBinding> {


    /**
     * 查询设备信息
     *
     * @param deviceBindingDTO
     * @return
     */
    List<DeviceBindingVO> pageDeviceBinding(DeviceBindingDTO deviceBindingDTO);
}