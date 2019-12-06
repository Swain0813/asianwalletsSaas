package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.*;
import com.asianwallets.base.service.DeviceInfoService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.entity.DeviceInfo;
import com.asianwallets.common.entity.Institution;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.BeanToMapUtil;
import com.asianwallets.common.utils.ReflexClazzUtils;
import com.asianwallets.common.vo.DeviceInfoExportVO;
import com.asianwallets.common.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        /*//判断名字是否为空
        if (deviceInfoDTO.getName() == null) {
            if (deviceInfoDTO.getModelName() == null || deviceInfoDTO.getVendorName() == null) {
                throw new BusinessException(EResultEnum.PARAMETER_IS_NOT_PRESENT.getCode());
            }
            //设置默认名称 厂商_型号
            deviceInfoDTO.setName(deviceInfoDTO.getVendorName() + "_" + deviceInfoDTO.getModelName());
        }*/
        Institution institutionInfo = institutionMapper.getInstitutionInfo(deviceInfoDTO.getInstitutionId(),auditorProvider.getLanguage());
        if (institutionInfo == null || !institutionInfo.getEnabled()) {
            throw new BusinessException(EResultEnum.INSTITUTION_STATUS_ABNORMAL.getCode());
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
    public List exportDeviceInfo(DeviceInfoDTO deviceInfoDTO) {
        deviceInfoDTO.setPageSize(Integer.MAX_VALUE);
        List<DeviceInfoVO> deviceInfoVOS = deviceInfoMapper.pageDeviceInfo(deviceInfoDTO);
        Map<String, String[]> result = ReflexClazzUtils.getFiledStructMap(DeviceInfoExportVO.class);
        //注释信息
        String[] comment = result.get(AsianWalletConstant.EXCEL_TITLES);
        //属性名信息
        String[] property = result.get(AsianWalletConstant.EXCEL_ATTRS);
        ArrayList<Object> oList1 = new ArrayList<>();
        LinkedHashSet<Object> oSet1 = new LinkedHashSet<>();
        for (DeviceInfoVO deviceInfoVO : deviceInfoVOS) {
            HashMap<String, Object> oMap = BeanToMapUtil.beanToMap(deviceInfoVO);
            ArrayList<Object> oList2 = new ArrayList<>();
            Set<String> keySet = oMap.keySet();
            for (int i = 0; i < property.length; i++) {
                for (String s : keySet) {
                    if (s.equals(property[i])) {
                        oSet1.add(comment[i]);
                        if (s.equals("bindingStatus")) {
                            if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                oList2.add("已绑定");
                            } else if ((String.valueOf((oMap.get(s))).equals("0"))) {
                                oList2.add("未绑定");
                            } else {
                                oList2.add("");
                            }
                        } else if (s.equals("enabled")) {
                            if ((String.valueOf((oMap.get(s))).equals("1"))) {
                                oList2.add("启用");
                            } else if ((String.valueOf((oMap.get(s))).equals("0"))) {
                                oList2.add("禁用");
                            } else {
                                oList2.add("");
                            }
                        } else {
                            oList2.add(oMap.get(s));
                        }
                    }
                }
            }
            oList1.add(oList2);
        }
        oList1.add(0, oSet1);
        return oList1;
    }
}
