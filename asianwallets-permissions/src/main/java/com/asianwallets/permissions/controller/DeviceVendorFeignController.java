package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.DeviceVendorFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shenxinran
 * @Date: 2019/3/6 09:55
 * @Description: 设备厂商管理Controller -- Feign
 */
@RestController
@Api(description = "设备厂商管理接口")
@RequestMapping("/devicevendor")
public class DeviceVendorFeignController extends BaseController {

    @Autowired
    private DeviceVendorFeign deviceVendorFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "新增设备厂商")
    @PostMapping("/addDeviceVendor")
    public BaseResponse addDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(deviceVendorDTO),
                "新增设备厂商"));
        return deviceVendorFeign.addDeviceVendor(deviceVendorDTO);
    }

    @ApiOperation(value = "启用禁用厂商")
    @PostMapping("/banDeviceVendor")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(deviceVendorDTO),
                "启用禁用厂商"));
        return deviceVendorFeign.banDeviceVendor(deviceVendorDTO);
    }

    @ApiOperation(value = "查询设备厂商")
    @PostMapping("/pageDeviceVendor")
    public BaseResponse pageDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(deviceVendorDTO),
                "查询设备厂商"));
        return deviceVendorFeign.pageDeviceVendor(deviceVendorDTO);
    }

    @ApiOperation(value = "修改设备厂商信息")
    @PostMapping("/updateDeviceVendor")
    public BaseResponse updateDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(deviceVendorDTO),
                "修改设备厂商信息"));
        return deviceVendorFeign.updateDeviceVendor(deviceVendorDTO);
    }

    @ApiOperation(value = "查询厂商类别")
    @GetMapping("/queryVendorCategory")
    public BaseResponse queryVendorCategory() {
        return deviceVendorFeign.queryVendorCategory();
    }
}
