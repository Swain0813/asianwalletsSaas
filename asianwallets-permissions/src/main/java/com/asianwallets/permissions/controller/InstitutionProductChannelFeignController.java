package com.asianwallets.permissions.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.InstitutionProductChannelFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/insProCha")
@Api(description = "机构产品通道管理")
public class InstitutionProductChannelFeignController extends BaseController {

    @Autowired
    private InstitutionProductChannelFeign institutionProductChannelFeign;

    @ApiOperation(value = "新增机构关联产品通道信息")
    @PostMapping("/addInsProCha")
    public BaseResponse addInstitutionProductChannel(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        return institutionProductChannelFeign.addInsProCha(institutionProductChannelDTOList);
    }

    @ApiOperation(value = "修改机构关联产品通道信息")
    @PostMapping("/updateInsProCha")
    public BaseResponse updateInsProChaByInsId(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        return institutionProductChannelFeign.updateInsProCha(institutionProductChannelDTOList);
    }

    @ApiOperation(value = "根据机构ID查询机构关联产品通道信息")
    @GetMapping("/getInsProChaByInsId")
    public BaseResponse getInsProChaByInsId(@RequestParam @ApiParam String insId) {
        return institutionProductChannelFeign.getInsProChaByInsId(insId);
    }
}
