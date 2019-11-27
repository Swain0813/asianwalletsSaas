package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.DeviceInfoMapper;
import com.asianwallets.base.dao.DeviceModelMapper;
import com.asianwallets.base.dao.DeviceVendorMapper;
import com.asianwallets.base.service.DeviceModelService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.entity.DeviceModel;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.DeviceModelVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 11:10
 * @Description: 设备型号管理 Service
 */
@Service
@Transactional
public class DeviceModelServiceImpl extends BaseServiceImpl<DeviceModel> implements DeviceModelService {

    @Autowired
    private DeviceVendorMapper deviceVendorMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 新增设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public int addDeviceModel(DeviceModelDTO deviceModelDTO) {
        if (deviceModelDTO.getAccessPermit() == null || deviceModelDTO.getCardReader() == null || deviceModelDTO.getDeviceName() == null || deviceModelDTO.getDeviceType()
                == null || deviceModelDTO.getNetwork() == null || deviceModelDTO.getPrinter() == null || deviceModelDTO.getRam() == null || deviceModelDTO.getResolutionRatio() == null ||
                deviceModelDTO.getSystem() == null || deviceModelDTO.getVendorId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        //判断设备厂商是否存在
        DeviceModel deviceModel = getDeviceModel(deviceModelDTO);
        deviceModel.setEnabled(true);
        //判断是否重复
        if (deviceModelMapper.selectOne(deviceModel) != null) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceModel.setCreateTime(new Date());
        return deviceModelMapper.insertSelective(deviceModel);
    }

    /**
     * 查询厂商是否存在
     *
     * @param deviceModelDTO
     * @return
     */
    private DeviceModel getDeviceModel(DeviceModelDTO deviceModelDTO) {
        if (deviceModelDTO.getVendorId() != null && deviceVendorMapper.selectByVendorId(deviceModelDTO.getVendorId()) <= 0) {
            throw new BusinessException(EResultEnum.DEVICE_VENDOR_NOT_EXIST.getCode());
        }
        DeviceModel deviceModel = new DeviceModel();
        BeanUtils.copyProperties(deviceModelDTO, deviceModel);
        return deviceModel;
    }

    /**
     * 启用禁用设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public int banDeviceModel(DeviceModelDTO deviceModelDTO) {
        //判断型号是否被绑定使用
        if (deviceInfoMapper.selectByModelId(deviceModelDTO.getId()) > 0) {
            throw new BusinessException(EResultEnum.DEVICE_HAS_BEEN_BOUND.getCode());
        }
        int infoNum = deviceInfoMapper.selectByModelIdAndStatus(deviceModelDTO.getId(), !deviceModelDTO.getEnabled());
        if (deviceInfoMapper.updateByModelId(deviceModelDTO.getId(), deviceModelDTO.getEnabled()) != infoNum) {
            throw new BusinessException(EResultEnum.DEVICE_OPERATION_FAILED.getCode());
        }
        DeviceModel deviceModel = new DeviceModel();
        BeanUtils.copyProperties(deviceModelDTO, deviceModel);
        deviceModel.setUpdateTime(new Date());
        return deviceModelMapper.updateByPrimaryKeySelective(deviceModel);
    }

    /**
     * 修改设备型号
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public int updateDeviceModel(DeviceModelDTO deviceModelDTO) {
        //判断型号是否被绑定使用
        if (deviceInfoMapper.selectByModelId(deviceModelDTO.getId()) > 0) {
            throw new BusinessException(EResultEnum.DEVICE_HAS_BEEN_BOUND.getCode());
        }
        //判断设备厂商是否存在
        DeviceModel deviceModel = getDeviceModel(deviceModelDTO);
        //判断重复
        if (deviceModelMapper.selectCount(deviceModel) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceModel.setUpdateTime(new Date());
        return deviceModelMapper.updateByPrimaryKeySelective(deviceModel);
    }

    /**
     * 查询设备型号信息
     *
     * @param deviceModelDTO
     * @return
     */
    @Override
    public PageInfo<DeviceModelVO> pageDeviceModel(DeviceModelDTO deviceModelDTO) {
        //设置语言
        deviceModelDTO.setLanguage(auditorProvider.getLanguage());
        return new PageInfo<>(deviceModelMapper.pageDeviceModel(deviceModelDTO));
    }

    /**
     * 查询设备型号类别
     *
     * @param
     * @return
     */
    @Override
    public List<DeviceModelVO> queryModelCategory() {
        return deviceModelMapper.queryModelCategory();
    }
}
