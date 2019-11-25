package com.asianwallets.permissions.service;
import com.asianwallets.common.entity.Holidays;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 节假日相关
 */
public interface HolidayFeignService {
    /**
     * 节假日的导入
     * @param file
     * @param name
     * @return
     */
    List<Holidays> uploadFiles(MultipartFile file, String name);
}
