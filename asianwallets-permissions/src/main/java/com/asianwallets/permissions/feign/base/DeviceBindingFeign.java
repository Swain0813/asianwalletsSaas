package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.DeviceBindingDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.DeviceBindingFeignImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 设备绑定Feign
 */
@Component
@FeignClient(value = "asianwallets-base", fallback = DeviceBindingFeignImpl.class)
public interface DeviceBindingFeign {

    /**
     * 添加设备绑定
     *
     * @param deviceBindingDTO
     * @return
     */
    @PostMapping("/devicebinding/addDeviceBinding")
    BaseResponse addDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO);

    /**
     * 接除设备绑定
     *
     * @param deviceBindingDTO
     * @return
     */
    @PostMapping("/devicebinding/banDeviceBinding")
    BaseResponse banDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO);

    /**
     * 查询设备绑定
     *
     * @param deviceBindingDTO
     * @return
     */
    @PostMapping("/devicebinding/pageDeviceBinding")
    BaseResponse pageDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO);
}
