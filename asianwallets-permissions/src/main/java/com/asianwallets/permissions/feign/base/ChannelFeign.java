package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.AgentChannelsDTO;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.permissions.feign.base.impl.ChannelFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "asianwallets-base", fallback = ChannelFeignImpl.class)
public interface ChannelFeign {

    @ApiOperation(value = "添加通道信息")
    @PostMapping("/channel/addChannel")
    BaseResponse addChannel(@RequestBody @ApiParam ChannelDTO channelDTO);

    @ApiOperation(value = "修改通道信息")
    @PostMapping("/channel/updateChannel")
    BaseResponse updateChannel(@RequestBody @ApiParam ChannelDTO channelDTO);

    @ApiOperation(value = "分页查询通道信息")
    @PostMapping("/channel/pageFindChannel")
    BaseResponse pageFindChannel(@RequestBody @ApiParam ChannelDTO channelDTO);

    @ApiOperation(value = "根据通道ID查询通道详情")
    @GetMapping("/channel/getChannelById")
    BaseResponse getChannelById(@RequestParam("channelId") @ApiParam String channelId);

    @ApiOperation(value = "导出通道信息")
    @PostMapping("/channel/exportChannel")
    List<ChannelExportVO> exportChannel(@RequestBody @ApiParam ChannelDTO channelDTO);

    @ApiOperation(value = "查询所有通道编号")
    @PostMapping("/channel/getAllChannelCode")
    List<String> getAllChannelCode();

    @PostMapping("/channel/pageAgentChannels")
    BaseResponse pageAgentChannels(@RequestBody @ApiParam AgentChannelsDTO agentChannelsDTO);
}
