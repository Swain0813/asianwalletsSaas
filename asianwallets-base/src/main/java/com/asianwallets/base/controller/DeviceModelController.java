package com.asianwallets.base.controller;

import com.asianwallets.base.service.DeviceModelService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DeviceModelDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shenxinran
 * @Date: 2019/3/6 11:03
 * @Description: 设备型号管理
 */
@RestController
@Api(description = "设备型号管理接口")
@RequestMapping("/devicemodel")
public class DeviceModelController extends BaseController {

    @Autowired
    private DeviceModelService deviceModelService;

    @ApiOperation(value = "新增设备型号")
    @PostMapping("/addDeviceModel")
    public BaseResponse addDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceModelService.addDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "启用禁用设备型号")
    @PostMapping("/banDeviceModel")
    public BaseResponse banDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceModelService.banDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "修改设备型号信息")
    @PostMapping("/updateDeviceModel")
    public BaseResponse updateDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        deviceModelDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceModelService.updateDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "查询设备型号信息")
    @PostMapping("/pageDeviceModel")
    public BaseResponse pageDeviceModel(@RequestBody @ApiParam DeviceModelDTO deviceModelDTO) {
        return ResultUtil.success(deviceModelService.pageDeviceModel(deviceModelDTO));
    }

    @ApiOperation(value = "查询厂商类别")
    @GetMapping("/queryModelCategory")
    public BaseResponse queryModelCategory() {
        return ResultUtil.success(deviceModelService.queryModelCategory());
    }

}
