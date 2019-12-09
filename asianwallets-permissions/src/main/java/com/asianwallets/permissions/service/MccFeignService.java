package com.asianwallets.permissions.service;

import com.asianwallets.common.entity.Mcc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 18:25
 * @Description: 设备信息Feign-Service
 */
public interface MccFeignService {

    List<Mcc> uploadMcc(MultipartFile file, String name);
}
