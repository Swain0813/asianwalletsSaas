package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.DeviceInfoMapper;
import com.asianwallets.base.dao.DeviceModelMapper;
import com.asianwallets.base.dao.DeviceVendorMapper;
import com.asianwallets.base.service.DeviceInfoService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.entity.DeviceInfo;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 15:22
 * @Description: 设备信息管理 Service
 */
@Service
@Transactional
public class DeviceInfoServiceImpl extends BaseServiceImpl<DeviceInfo> implements DeviceInfoService {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private DeviceVendorMapper deviceVendorMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    /**
     * 新增设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public int addDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        //判断数据是否存在
        if (deviceInfoDTO.getImei() == null || deviceInfoDTO.getMac() == null || deviceInfoDTO.getSn()
                == null || deviceInfoDTO.getVendorId() == null || deviceInfoDTO.getModelId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //判断名字是否为空
        if (deviceInfoDTO.getName() == null) {
            if (deviceInfoDTO.getModelName() == null || deviceInfoDTO.getVendorName() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //设置默认名称 厂商_型号
            deviceInfoDTO.setName(deviceInfoDTO.getVendorName() + "_" + deviceInfoDTO.getModelName());
        }
        //判断设备厂商是否存在
        DeviceInfo deviceInfo = getDeviceInfo(deviceInfoDTO);
        deviceInfo.setEnabled(true);
        deviceInfo.setBindingStatus(false);

        if (deviceInfoMapper.selectOne(deviceInfo) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        if (deviceInfoMapper.selectByInfoIMEI(deviceInfo.getImei()) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceInfo.setCreateTime(new Date());
        return deviceInfoMapper.insertSelective(deviceInfo);
    }

    /**
     * 判断设备厂商是否存在
     *
     * @param deviceInfoDTO
     * @return
     */
    private DeviceInfo getDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        //判断设备厂商是否存在
        if (deviceInfoDTO.getVendorId() != null && deviceVendorMapper.selectByVendorId(deviceInfoDTO.getVendorId()) <= 0) {
            throw new BusinessException(EResultEnum.DEVICE_VENDOR_NOT_EXIST.getCode());
        }
        //判断设备型号是否存在
        if (deviceInfoDTO.getModelId() != null && deviceModelMapper.selectByModelId(deviceInfoDTO.getModelId()) <= 0) {
            throw new BusinessException(EResultEnum.DEVICE_MODEL_NOT_EXIST.getCode());
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        BeanUtils.copyProperties(deviceInfoDTO, deviceInfo);
        return deviceInfo;
    }

    /**
     * 启用禁用设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public int banDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        //判断设备是否被绑定
        if (deviceInfoMapper.selectByInfoId(deviceInfoDTO.getId()) > 0) {
            throw new BusinessException(EResultEnum.DEVICE_HAS_BEEN_BOUND.getCode());
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        BeanUtils.copyProperties(deviceInfoDTO, deviceInfo);
        deviceInfo.setUpdateTime(new Date());
        return deviceInfoMapper.updateByPrimaryKeySelective(deviceInfo);
    }

    /**
     * 修改设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public int updateDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        //判断设备是否被绑定
        if (deviceInfoMapper.selectByInfoId(deviceInfoDTO.getId()) > 0) {
            throw new BusinessException(EResultEnum.DEVICE_HAS_BEEN_BOUND.getCode());
        }
        //判断设备厂商是否存在
        DeviceInfo deviceInfo = getDeviceInfo(deviceInfoDTO);
        deviceInfo.setUpdateTime(new Date());
        return deviceInfoMapper.updateByPrimaryKeySelective(deviceInfo);
    }

    /**
     * 查询设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public PageInfo<DeviceInfoVO> pageDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        return new PageInfo<>(deviceInfoMapper.pageDeviceInfo(deviceInfoDTO));
    }

    /**
     * 导入设备信息
     *
     * @param fileList
     * @return
     */
    @Override
    public int uploadFiles(List<DeviceInfo> fileList) {
        return deviceInfoMapper.insertList(fileList);
    }


}
