package com.asianwallets.permissions.service;

import com.asianwallets.common.entity.DeviceInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:25
 * @Description: 设备信息Feign-Service
 */
public interface DeviceInfoFeignService {

    List<DeviceInfo> uploadDeviceInfo(MultipartFile file, String name);
}
