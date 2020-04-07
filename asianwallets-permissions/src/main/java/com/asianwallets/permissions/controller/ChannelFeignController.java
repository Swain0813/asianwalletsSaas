package com.asianwallets.permissions.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.cache.CommonLanguageCacheService;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.AgentChannelsDTO;
import com.asianwallets.common.dto.ChannelDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.utils.ArrayUtil;
import com.asianwallets.common.utils.ExcelUtils;
import com.asianwallets.common.utils.SpringContextUtil;
import com.asianwallets.common.vo.ChannelExport;
import com.asianwallets.common.vo.ChannelExportVO;
import com.asianwallets.permissions.feign.base.ChannelFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@RestController
@Api(description = "通道接口")
@RequestMapping("/channel")
@Slf4j
public class ChannelFeignController extends BaseController {

    @Autowired
    private ChannelFeign channelFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加通道信息")
    @PostMapping("/addChannel")
    public BaseResponse addChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(channelDTO),
                "添加通道信息"));
        return channelFeign.addChannel(channelDTO);
    }

    @ApiOperation(value = "修改通道信息")
    @PostMapping("/updateChannel")
    public BaseResponse updateChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(channelDTO),
                "修改通道信息"));
        return channelFeign.updateChannel(channelDTO);
    }

    @ApiOperation(value = "分页查询通道信息")
    @PostMapping("/pageFindChannel")
    public BaseResponse pageFindChannel(@RequestBody @ApiParam ChannelDTO channelDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelDTO),
                "分页查询通道信息"));
        return channelFeign.pageFindChannel(channelDTO);
    }

    @ApiOperation(value = "根据通道ID查询通道详情")
    @GetMapping("/getChannelById")
    public BaseResponse getChannelById(@RequestParam @ApiParam String channelId) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelId),
                "根据通道ID查询通道详情"));
        return channelFeign.getChannelById(channelId);
    }

    @ApiOperation(value = "导出通道信息")
    @PostMapping("/exportChannel")
    public BaseResponse exportChannel(@RequestBody @ApiParam ChannelDTO channelDTO, HttpServletResponse response) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(channelDTO),
                "导出通道信息"));
        ExcelWriter writer = ExcelUtil.getBigWriter();
        try {
            List<ChannelExportVO> dataList = channelFeign.exportChannel(channelDTO);
            ServletOutputStream out = response.getOutputStream();
            if (ArrayUtil.isEmpty(dataList)) {
                HashMap errorMsgMap = SpringContextUtil.getBean(CommonLanguageCacheService.class).getLanguage(getLanguage());
                writer.write(Arrays.asList("message", errorMsgMap.get(String.valueOf(EResultEnum.DATA_IS_NOT_EXIST.getCode()))));
                writer.flush(out);
                return ResultUtil.success();
            }
            ExcelUtils excelUtils = new ExcelUtils();
            excelUtils.exportExcel(dataList, ChannelExport.class, writer);
            writer.flush(out);
        } catch (Exception e) {
            log.info("==========【导出通道信息】==========【导出通道信息异常】", e);
            throw new BusinessException(EResultEnum.INSTITUTION_INFORMATION_EXPORT_FAILED.getCode());
        } finally {
            writer.close();
        }
        return ResultUtil.success();
    }

    @ApiOperation(value = "代理商渠道查询")
    @PostMapping("/pageAgentChannels")
    public BaseResponse pageAgentChannels(@RequestBody @ApiParam AgentChannelsDTO agentChannelsDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(agentChannelsDTO),
                "代理商渠道查询"));
        return channelFeign.pageAgentChannels(agentChannelsDTO);
    }
}
