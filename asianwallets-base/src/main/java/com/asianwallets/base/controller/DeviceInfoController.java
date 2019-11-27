package com.asianwallets.base.controller;

import com.asianwallets.base.service.DeviceInfoService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.entity.DeviceInfo;
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

import java.util.List;

/**
 * @author shenxinran
 * @Date: 2019/3/6 15:19
 * @Description: 设备信息管理接口
 */
@RestController
@Api(description = "设备信息管理接口")
@RequestMapping("/deviceinfo")
public class DeviceInfoController extends BaseController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @ApiOperation(value = "新增设备信息")
    @PostMapping("/addDeviceInfo")
    public BaseResponse addDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        deviceInfoDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceInfoService.addDeviceInfo(deviceInfoDTO));
    }

    @ApiOperation(value = "启用禁用设备信息")
    @PostMapping("/banDeviceInfo")
    public BaseResponse banDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        deviceInfoDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceInfoService.banDeviceInfo(deviceInfoDTO));
    }

    @ApiOperation(value = "修改设备信息")
    @PostMapping("/updateDeviceInfo")
    public BaseResponse updateDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        deviceInfoDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceInfoService.updateDeviceInfo(deviceInfoDTO));
    }

    @ApiOperation(value = "查询设备信息")
    @PostMapping("/pageDeviceInfo")
    public BaseResponse pageDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        return ResultUtil.success(deviceInfoService.pageDeviceInfo(deviceInfoDTO));
    }

    @ApiOperation(value = "导入设备信息")
    @PostMapping("/uploadDeviceInfo")
    public BaseResponse uploadDeviceInfo(@RequestBody @ApiParam List<DeviceInfo> fileList) {
        return ResultUtil.success(deviceInfoService.uploadFiles(fileList));
    }
}
