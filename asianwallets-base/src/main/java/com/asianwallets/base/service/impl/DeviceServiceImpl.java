package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.DeviceVendorMapper;
import com.asianwallets.base.service.DeviceService;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.entity.DeviceVendor;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName DeviceServiceImpl
 * @Description 新增厂商
 * @Author abc
 * @Date 2019/11/22 15:28
 * @Version 1.0
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceVendorMapper deviceVendorMapper;


    /**
     * 新增厂商
     *
     * @param deviceVendorDTO
     * @return 条数
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
        if (deviceVendorMapper.selectCountByCnNameAndEnName(deviceVendor) > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        deviceVendor.setCreateTime(new Date());
        deviceVendor.setEnabled(true);
        deviceVendor.setId(IDS.getRandomInt(15));
        return deviceVendorMapper.insertSelective(deviceVendor);
    }

    /**
     * 修改厂商
     *
     * @param deviceVendorDTO
     * @return 影响条数
     */
    @Override
    public int updateDeviceVendor(DeviceVendorDTO deviceVendorDTO) {
        return 0;
    }
}
