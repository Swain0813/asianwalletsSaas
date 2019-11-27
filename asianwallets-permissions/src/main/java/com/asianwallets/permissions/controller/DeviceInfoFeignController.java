package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.DeviceInfoDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.DeviceInfoFeign;
import com.asianwallets.permissions.service.DeviceInfoFeignService;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author shenxinran
 * @Date: 2019/3/6 15:19
 * @Description: 设备信息管理接口
 */
@RestController
@Api(description = "设备信息管理接口")
@RequestMapping("/deviceinfo")
public class DeviceInfoFeignController extends BaseController {

    @Autowired
    private DeviceInfoFeign deviceInfoFeign;

    @Autowired
    private DeviceInfoFeignService deviceInfoFeignService;

    @Autowired
    private OperationLogService operationLogService;

    @Value("${file.tmpfile}")
    private String tmpfile;//springboot启动的临时文件存放

    @ApiOperation(value = "新增设备信息")
    @PostMapping("/addDeviceInfo")
    public BaseResponse addDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(deviceInfoDTO),
                "新增设备信息"));
        return deviceInfoFeign.addDeviceInfo(deviceInfoDTO);
    }

    @ApiOperation(value = "启用禁用设备信息")
    @PostMapping("/banDeviceInfo")
    public BaseResponse banDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(deviceInfoDTO),
                "启用禁用设备信息"));
        return deviceInfoFeign.banDeviceInfo(deviceInfoDTO);
    }

    @ApiOperation(value = "修改设备信息")
    @PostMapping("/updateDeviceInfo")
    public BaseResponse updateDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(deviceInfoDTO),
                "修改设备信息"));
        return deviceInfoFeign.updateDeviceInfo(deviceInfoDTO);
    }

    @ApiOperation(value = "查询设备信息")
    @PostMapping("/pageDeviceInfo")
    public BaseResponse pageDeviceInfo(@RequestBody @ApiParam DeviceInfoDTO deviceInfoDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(deviceInfoDTO),
                "查询设备信息"));
        return deviceInfoFeign.pageDeviceInfo(deviceInfoDTO);
    }

    @ApiOperation(value = "导入设备信息")
    @PostMapping("/uploadDeviceInfo")
    public BaseResponse uploadDeviceInfo(@RequestParam("file") @ApiParam MultipartFile file) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, null,
                "导入设备信息"));
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(tmpfile);//指定临时文件路径，这个路径可以随便写
        factory.createMultipartConfig();
        return deviceInfoFeign.uploadDeviceInfo(deviceInfoFeignService.uploadDeviceInfo(file, this.getSysUserVO().getUsername()));
    }
}
