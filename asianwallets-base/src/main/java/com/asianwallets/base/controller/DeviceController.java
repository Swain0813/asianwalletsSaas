package com.asianwallets.base.controller;

import com.asianwallets.base.service.DeviceService;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DeviceController
 * @Description 设备管理
 * @Author abc
 * @Date 2019/11/22 15:13
 * @Version 1.0
 */
@RestController
@RequestMapping("/device")
@Api("设备管理接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    //-------------------------厂商-------------------------//

    @ApiOperation(value = "新增厂商")
    @PostMapping("addDeviceVendor")
    public BaseResponse addDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        return ResultUtil.success(deviceService.addDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "修改厂商")
    @PostMapping("updateDeviceVendor")
    public BaseResponse updateDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        return ResultUtil.success(deviceService.updateDeviceVendor(deviceVendorDTO));
    }
}
