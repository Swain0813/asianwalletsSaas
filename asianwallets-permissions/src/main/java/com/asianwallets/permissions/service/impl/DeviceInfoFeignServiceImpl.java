package com.asianwallets.permissions.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.entity.DeviceInfo;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.permissions.dao.DeviceInfoMapper;
import com.asianwallets.permissions.dao.DeviceModelMapper;
import com.asianwallets.permissions.dao.DeviceVendorMapper;
import com.asianwallets.permissions.service.DeviceInfoFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:26
 * @Description: 设备信息
 */
@Service
@Transactional
public class DeviceInfoFeignServiceImpl implements DeviceInfoFeignService {

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceVendorMapper deviceVendorMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Override
    public List<DeviceInfo> uploadDeviceInfo(MultipartFile file, String createName) {
        ArrayList<DeviceInfo> h = new ArrayList<>();
        String vendorId;
        String modelId;
        // 判断格式0
        if (!file.getOriginalFilename().matches("^.+\\.(?i)(xls)$") && !file.getOriginalFilename().matches("^.+\\.(?i)(xlsx)$")) {
            throw new BusinessException(EResultEnum.FILE_FORMAT_ERROR.getCode());
        }
        ExcelReader reader;
        try {
            reader = ExcelUtil.getReader(file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        List<List<Object>> read = reader.read();
        //判断是否超过上传限制
        if (read.size() - 1 > AsianWalletConstant.UPLOAD_LIMIT) {
            throw new BusinessException(EResultEnum.EXCEEDING_UPLOAD_LIMIT.getCode());
        }
        if (read.size() <= 0) {
            throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
        }
        for (int i = 1; i < read.size(); i++) {
            List<Object> objects = read.get(i);
            //判断传入的excel的格式是否符合约定
            if (StringUtils.isEmpty(objects.get(0))
                    || StringUtils.isEmpty(objects.get(1))
                    || StringUtils.isEmpty(objects.get(3))
                    || StringUtils.isEmpty(objects.get(4))
                    || objects.size() != 6
                    || StringUtils.isEmpty(objects.get(5))) {
                throw new BusinessException(EResultEnum.EXCEL_FORMAT_INCORRECT.getCode());
            }
            //如果名称没有填写
            if (StringUtils.isEmpty(objects.get(2))) {
                objects.set(2, objects.get(0) + "_" + objects.get(1));
            }
            DeviceInfo deviceInfo = new DeviceInfo();
            vendorId = checkVendor(objects.get(0).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            modelId = checkModel(objects.get(1).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            //校验IMEL
            if (deviceInfoMapper.selectByIMEL(objects.get(3)) > 0) {
                throw new BusinessException(EResultEnum.DEVICE_IMEL_EXIST.getCode());
            }
            //校验SN
            if (deviceInfoMapper.selectBySN(objects.get(4)) > 0) {
                throw new BusinessException(EResultEnum.DEVICE_SN_EXIST.getCode());
            }
            deviceInfo.setId(IDS.uuid2());
            deviceInfo.setVendorId(vendorId);
            deviceInfo.setModelId(modelId);
            deviceInfo.setImei(objects.get(3).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            deviceInfo.setSn(objects.get(4).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            deviceInfo.setMac(objects.get(5).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            deviceInfo.setName(objects.get(2).toString().replaceAll("/(^\\s*)|(\\s*$)/g", ""));
            deviceInfo.setEnabled(true);
            deviceInfo.setCreateTime(new Date());
            deviceInfo.setCreator(createName);
            deviceInfo.setBindingStatus(false);
            h.add(deviceInfo);
        }
        if (h.size() == 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }

        Set<DeviceInfo> set = new HashSet<>();
        h.stream().forEach(p ->
                set.add(p)
        );
        if (set.size() != h.size()) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        return h;
    }

    /**
     * 校验型号名
     *
     * @param model
     * @return
     */
    private String checkModel(String model) {
        //判断型号存不存在
        String modelId = deviceModelMapper.selectByModelName(model);
        if (modelId == null) {
            throw new BusinessException(EResultEnum.DEVICE_MODEL_NOT_EXIST.getCode());
        }
        return modelId;
    }

    /**
     * 检验厂商名
     *
     * @param vendor
     * @return
     */
    private String checkVendor(String vendor) {
        //判断厂商存不存在
        String vendorId = deviceVendorMapper.selectByVendorName(vendor);
        if (vendorId == null) {
            throw new BusinessException(EResultEnum.DEVICE_VENDOR_NOT_EXIST.getCode());
        }
        return vendorId;
    }


}
