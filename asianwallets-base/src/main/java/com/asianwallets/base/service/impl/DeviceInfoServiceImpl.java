package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.*;
import com.asianwallets.base.service.DeviceInfoService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.entity.DeviceInfo;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AuditorProvider auditorProvider;

    /**
     * 新增设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public int addDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        //判断数据是否存在
        if (deviceInfoDTO.getImei() == null || deviceInfoDTO.getSn()
                == null || deviceInfoDTO.getVendorId() == null || deviceInfoDTO.getModelId() == null || deviceInfoDTO.getInstitutionId() == null) {
            throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
        }
        Institution institutionInfo = institutionMapper.getInstitutionInfo(deviceInfoDTO.getInstitutionId(), auditorProvider.getLanguage());
        if (institutionInfo == null || !institutionInfo.getEnabled()) {
            throw new BusinessException(EResultEnum.INSTITUTION_STATUS_ABNORMAL.getCode());
        }
        //判断设备厂商是否存在
        DeviceInfo deviceInfo = getDeviceInfo(deviceInfoDTO);
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
        //未绑定是否走过交易
        if (ordersMapper.selectByDeviceCode(deviceInfoDTO.getImei()).size() != 0) {
            throw new BusinessException(EResultEnum.DEVICE_UNBIND_FAILED.getCode());
        }
        //判断imei 与 sn 是否重复
        if (!StringUtils.isBlank(deviceInfoDTO.getImei())) {
            if (deviceInfoMapper.selectByIMEI(deviceInfoDTO) != 0) {
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
        }
        if (!StringUtils.isBlank(deviceInfoDTO.getSn())) {
            if (deviceInfoMapper.selectBySN(deviceInfoDTO) != 0) {
                throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
            }
        }
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
        deviceInfoDTO.setSort("i.create_time");
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

    /**
     * 导出设备信息
     *
     * @param deviceInfoDTO
     * @return
     */
    @Override
    public List<DeviceInfoVO> exportDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        deviceInfoDTO.setPageSize(Integer.MAX_VALUE);
        deviceInfoDTO.setSort("i.create_time");
        List<DeviceInfoVO> deviceInfoVOS = deviceInfoMapper.pageDeviceInfo(deviceInfoDTO);
        List<DeviceInfoVO> collect = deviceInfoVOS.stream().sorted(Comparator.comparing(DeviceInfoVO::getCreateTime).reversed()).collect(Collectors.toList());
        for (DeviceInfoVO deviceInfoVO : collect) {
            if (deviceInfoVO.getBindingStatus()) {
                deviceInfoVO.setBindingStatusStr("已绑定");
            } else {
                deviceInfoVO.setBindingStatusStr("未绑定");
            }
            if (deviceInfoVO.getEnabled()) {
                deviceInfoVO.setEnabledStr("已启用");
            } else {
                deviceInfoVO.setEnabledStr("已禁用");
            }
        }
        return collect;
    }
}
