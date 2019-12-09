package com.asianwallets.base.controller;

import com.asianwallets.base.service.ChannelService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(description = "通道接口")
@RequestMapping("/channel")
public class ChannelController extends BaseController {

    @Autowired
    private ChannelService channelService;

    @ApiOperation(value = "添加通道信息")
    @PostMapping("/addChannel")
    public BaseResponse addChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        return ResultUtil.success(channelService.addChannel(getSysUserVO().getUsername(), channelDTO));
    }

    @ApiOperation(value = "修改通道信息")
    @PostMapping("/updateChannel")
    public BaseResponse updateChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        return ResultUtil.success(channelService.updateChannel(getSysUserVO().getUsername(), channelDTO));
    }

    @ApiOperation(value = "分页查询通道信息")
    @PostMapping("/pageFindChannel")
    public BaseResponse pageFindChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        return ResultUtil.success(channelService.pageFindChannel(channelDTO));
    }

    @ApiOperation(value = "根据通道ID查询通道详情")
    @GetMapping("/getChannelById")
    public BaseResponse getChannelById(@RequestParam @ApiParam String channelId) {
        return ResultUtil.success(channelService.getChannelById(channelId));
    }
}
