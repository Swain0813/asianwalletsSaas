package com.asianwallets.rights.controller;
import com.asianwallets.common.dto.OtaChannelDTO;
import com.asianwallets.common.entity.OtaChannel;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.rights.service.OtaChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.asianwallets.common.base.BaseController;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Api(description = "OTA平台")
@RestController
@RequestMapping("/otaChannel")
public class OtaChannelController extends BaseController {


    @Autowired
    private OtaChannelService otaChannelService;

    @ApiOperation(value = "OTA平台分页查询")
    @PostMapping("pageOtaChannel")
    public BaseResponse pageOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO) {
        return ResultUtil.success(otaChannelService.pageOtaChannel(otaChannelDTO));
    }

    @ApiOperation(value = "添加修改OTA平台")
    @PostMapping("addOtaChannel")
    public BaseResponse addOtaChannel(@RequestBody @ApiParam OtaChannelDTO otaChannelDTO) {
        otaChannelDTO.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(otaChannelService.addOtaChannel(otaChannelDTO));
    }


    @ApiOperation(value = "发放平台的下来框")
    @PostMapping("getOtaChannels")
    public List<OtaChannel> getOtaChannels() {
        return otaChannelService.getOtaChannels();
    }



}
