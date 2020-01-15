package com.asianwallets.base.controller;
import com.asianwallets.base.service.DeviceVendorService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.DeviceVendorDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author shenxinran
 * @Date: 2019/3/5 17:36
 * @Description: 设备厂商Controller
 */
@RestController
@Api(description = "设备厂商管理接口")
@RequestMapping("/devicevendor")
public class DeviceVendorController extends BaseController {

    @Autowired
    private DeviceVendorService deviceVendorService;

    @ApiOperation(value = "新增设备厂商")
    @PostMapping("/addDeviceVendor")
    public BaseResponse addDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceVendorService.addDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "启用禁用厂商")
    @PostMapping("/banDeviceVendor")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceVendorService.banDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "查询设备厂商")
    @PostMapping("/pageDeviceVendor")
    public BaseResponse pageDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        return ResultUtil.success(deviceVendorService.pageDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "修改设备厂商信息")
    @PostMapping("/updateDeviceVendor")
    public BaseResponse updateDeviceVendor(@RequestBody @ApiParam DeviceVendorDTO deviceVendorDTO) {
        deviceVendorDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(deviceVendorService.updateDeviceVendor(deviceVendorDTO));
    }

    @ApiOperation(value = "查询厂商类别")
    @GetMapping("/queryVendorCategory")
    public BaseResponse queryVendorCategory() {
        return ResultUtil.success(deviceVendorService.queryVendorCategory());
    }
}
