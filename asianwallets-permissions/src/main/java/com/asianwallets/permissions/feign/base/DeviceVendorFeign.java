package com.asianwallets.permissions.feign.base;


import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.DeviceVendorFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 设备厂商 Feign接口
 */
@Component
@FeignClient(value = "asianwallets-base", fallback = DeviceVendorFeignImpl.class)
public interface DeviceVendorFeign {
    /**
     * 添加
     *
     * @param deviceVendorDTO
     * @return
     */
    @PostMapping("/devicevendor/addDeviceVendor")
    BaseResponse addDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO);

    /**
     * 启用禁用
     *
     * @param deviceVendorDTO
     * @return
     */
    @PostMapping("/devicevendor/banDeviceVendor")
    BaseResponse banDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO);

    /**
     * 查询
     *
     * @param deviceVendorDTO
     * @return
     */
    @PostMapping("/devicevendor/pageDeviceVendor")
    BaseResponse pageDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO);

    /**
     * 更新
     *
     * @param deviceVendorDTO
     * @return
     */
    @PostMapping("/devicevendor/updateDeviceVendor")
    BaseResponse updateDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO);


    /**
     * 查询设备型号类型
     *
     * @return
     */
    @GetMapping("/devicevendor/queryVendorCategory")
    BaseResponse queryVendorCategory();
}
