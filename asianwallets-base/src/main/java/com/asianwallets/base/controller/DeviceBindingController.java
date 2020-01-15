package com.asianwallets.base.controller;

import com.asianwallets.base.service.DeviceBindingService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DeviceBindingDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author shenxinran
 * @Date: 2019/3/8 13:52
 * @Description: 设备绑定管理接口
 */
@RestController
@Api(description = "设备绑定管理接口")
@RequestMapping("/devicebinding")
public class DeviceBindingController extends BaseController {

    @Autowired
    private DeviceBindingService deviceBindingService;

    @ApiOperation(value = "新增设备绑定")
    @PostMapping("/addDeviceBinding")
    public BaseResponse addDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO) {
        deviceBindingDTO.setCreator(this.getUserName());
        return ResultUtil.success(deviceBindingService.addDeviceBinding(deviceBindingDTO));
    }

    @ApiOperation(value = "解绑设备")
    @PostMapping("/banDeviceBinding")
    public BaseResponse banDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO) {
        deviceBindingDTO.setModifier(this.getUserName());
        return ResultUtil.success(deviceBindingService.banDeviceBinding(deviceBindingDTO));
    }

    @ApiOperation(value = "查询设备绑定信息")
    @PostMapping("/pageDeviceBinding")
    public BaseResponse pageDeviceBinding(@RequestBody @ApiParam DeviceBindingDTO deviceBindingDTO) {
        if (StringUtils.isEmpty(deviceBindingDTO.getLanguage())) {
            deviceBindingDTO.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(deviceBindingService.pageDeviceBinding(deviceBindingDTO));
    }

}

