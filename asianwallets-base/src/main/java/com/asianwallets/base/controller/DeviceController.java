package com.asianwallets.base.controller;

import com.asianwallets.base.service.DeviceService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;

    //-------------------------厂商-------------------------//

    @ApiOperation(value = "新增厂商")
    @PostMapping("addDeviceVendor")
    public BaseResponse addDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.addDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "修改厂商")
    @PutMapping("updateDeviceVendor")
    public BaseResponse updateDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.updateDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "查询厂商")
    @PostMapping("pageDeviceVendor")
    public BaseResponse pageDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        return ResultUtil.success(deviceService.pageDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "启用禁用厂商")
    @PutMapping("banDeviceVendor")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.banDeviceVendor(deviceVendorDTO));
    }

    //-------------------------型号-------------------------//

    @ApiOperation(value = "新增型号")
    @PostMapping("/addDeviceModel")
    public BaseResponse addDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.addDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "启用禁用型号")
    @PostMapping("/banDeviceModel")
    public BaseResponse banDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.banDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "修改型号信息")
    @PostMapping("/updateDeviceModel")
    public BaseResponse updateDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceService.updateDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "查询型号信息")
    @PostMapping("/pageDeviceModel")
    public BaseResponse pageDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        return ResultUtil.success(deviceService.pageDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "查询厂商类别")
    @GetMapping("/queryModelCategory")
    public BaseResponse queryModelCategory() {
        return ResultUtil.success(deviceService.queryModelCategory());
    }

}
