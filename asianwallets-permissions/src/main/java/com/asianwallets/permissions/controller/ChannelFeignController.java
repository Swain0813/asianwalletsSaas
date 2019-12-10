package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.permissions.feign.base.ChannelFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(description = "通道接口")
@RequestMapping("/channel")
public class ChannelFeignController extends BaseController {

    @Autowired
    private ChannelFeign channelFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加通道信息")
    @PostMapping("/addChannel")
    public BaseResponse addChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(channelDTO),
                "添加通道信息"));
        return channelFeign.addChannel(channelDTO);
    }

    @ApiOperation(value = "修改通道信息")
    @PostMapping("/updateChannel")
    public BaseResponse updateChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(channelDTO),
                "修改通道信息"));
        return channelFeign.updateChannel(channelDTO);
    }

    @ApiOperation(value = "分页查询通道信息")
    @PostMapping("/pageFindChannel")
    public BaseResponse pageFindChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelDTO),
                "分页查询通道信息"));
        return channelFeign.pageFindChannel(channelDTO);
    }

    @ApiOperation(value = "根据通道ID查询通道详情")
    @GetMapping("/getChannelById")
    public BaseResponse getChannelById(@RequestParam @ApiParam String channelId) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelId),
                "根据通道ID查询通道详情"));
        return channelFeign.getChannelById(channelId);
    }

    @ApiOperation(value = "导出通道信息")
    @PostMapping("/exportChannel")
    public List<ChannelExportVO> exportChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelDTO),
                "导出通道信息"));
        return channelFeign.exportChannel(channelDTO);
    }
}
