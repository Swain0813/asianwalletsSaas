package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.DeviceInfoMapper;
import com.asianwallets.base.dao.DeviceModelMapper;
import com.asianwallets.base.dao.DeviceVendorMapper;
import com.asianwallets.base.service.DeviceVendorService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.entity.DeviceVendor;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/5 17:39
 * @Description: 设备厂商管理 Service
 */
@Service
@Transactional
public class DeviceVendorServiceImpl extends BaseServiceImpl<DeviceVendor> implements DeviceVendorService {

    @Autowired
    private DeviceVendorMapper deviceVendorMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    /**
     * 添加设备厂商信息
     *
     * @param deviceVendorDTO
     * @return
     */
    @Override
    public int addDeviceVendor(DeviceVendorDTO deviceVendorDTO) {
        //数据校验
        if (deviceVendorDTO.getBusinessContact() == null || deviceVendorDTO.getContactInformation() == null
                || deviceVendorDTO.getVendorCnName() == null || deviceVendorDTO.getVendorEnName() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        DeviceVendor deviceVendor = new DeviceVendor();
        BeanUtils.copyProperties(deviceVendorDTO, deviceVendor);
        //判断重复
        if (deviceVendorMapper.selectCount(deviceVendor) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceVendor.setCreateTime(new Date());
        deviceVendor.setEnabled(true);
        if (deviceVendorMapper.selectCount(deviceVendor) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceVendor.setId(IDS.uniqueID() + "");
        return deviceVendorMapper.insertSelective(deviceVendor);
    }

    /**
     * 启用禁用设备厂商
     *
     * @param deviceVendorDTO
     * @return
     */
    @Override
    public int banDeviceVendor(DeviceVendorDTO deviceVendorDTO) {
        //判断该厂商下的设备是否已经被绑定
        if (!deviceVendorDTO.getEnabled() && deviceInfoMapper.selectByVendorId(deviceVendorDTO.getId()) > 0) {
            throw new BusinessException(EResultEnum.DEVICE_HAS_BEEN_BOUND.getCode());
        }
        //判断该厂商下的型号是否已经被禁用
        int modelNum = deviceModelMapper.selectByVendorId(deviceVendorDTO.getId(), !deviceVendorDTO.getEnabled());
        if (deviceModelMapper.updateByVendorId(deviceVendorDTO.getId(), deviceVendorDTO.getEnabled()) != modelNum) {
            throw new BusinessException(EResultEnum.DEVICE_OPERATION_FAILED.getCode());
        }
        DeviceVendor deviceVendor = new DeviceVendor();
        BeanUtils.copyProperties(deviceVendorDTO, deviceVendor);
        deviceVendor.setUpdateTime(new Date());
        return deviceVendorMapper.updateByPrimaryKeySelective(deviceVendor);
    }

    /**
     * 查询设备厂商
     *
     * @param deviceVendorDTO
     * @return
     */
    @Override
    public PageInfo<DeviceVendor> pageDeviceVendor(DeviceVendorDTO deviceVendorDTO) {
        return new PageInfo<DeviceVendor>(deviceVendorMapper.pageDeviceVendor(deviceVendorDTO));
    }

    /**
     * 修改设备厂商信息
     *
     * @param deviceVendorDTO
     * @return
     */
    @Override
    public int updateDeviceVendor(DeviceVendorDTO deviceVendorDTO) {
        DeviceVendor deviceVendor = new DeviceVendor();
        BeanUtils.copyProperties(deviceVendorDTO, deviceVendor);
        //防止厂商的中英文名称被修改
        deviceVendor.setVendorCnName(null);
        deviceVendor.setVendorEnName(null);
        deviceVendor.setUpdateTime(new Date());
        return deviceVendorMapper.updateByPrimaryKeySelective(deviceVendor);
    }

    /**
     * 查询设备厂商类别
     *
     * @param
     * @return
     */
    @Override
    public List<DeviceVendor> queryVendorCategory() {
        return deviceVendorMapper.queryVendorCategory();
    }
}
