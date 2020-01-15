package com.asianwallets.base.controller;

import com.asianwallets.base.service.InstitutionProductChannelService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.InstitutionChannelQueryDTO;
import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.dto.InstitutionProductDTO;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/insProCha")
@Api(description = "机构产品通道管理")
public class InstitutionProductChannelController extends BaseController {

    @Autowired
    private InstitutionProductChannelService institutionProductChannelService;

    @ApiOperation(value = "新增机构关联产品通道信息")
    @PostMapping("/addInsProCha")
    public BaseResponse addInstitutionProductChannel(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        return ResultUtil.success(institutionProductChannelService.addInstitutionProductChannel(this.getUserName(), institutionProductChannelDTOList));
    }

    @ApiOperation(value = "修改机构关联产品通道信息")
    @PostMapping("/updateInsProCha")
    public BaseResponse updateInsProChaByInsId(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        return ResultUtil.success(institutionProductChannelService.updateInsProChaByInsId(this.getUserName(), institutionProductChannelDTOList));
    }

    @ApiOperation(value = "根据机构ID查询机构关联产品通道信息")
    @GetMapping("/getInsProChaByInsId")
    public BaseResponse getInsProChaByInsId(@RequestParam(required = false) @ApiParam String insId, @RequestParam(required = false) @ApiParam String merId) {
        return ResultUtil.success(institutionProductChannelService.getInsProChaByInsId(insId, merId));
    }

    @ApiOperation(value = "查询所有产品关联通道信息")
    @PostMapping("/getAllProCha")
    public BaseResponse getAllProCha() {
        return ResultUtil.success(institutionProductChannelService.getAllProCha());
    }

    @ApiOperation(value = "分页查询机构参数设置")
    @PostMapping("/pageInstitutionRequests")
    public BaseResponse pageInstitutionRequests(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO) {
        return ResultUtil.success(institutionProductChannelService.pageInstitutionRequests(institutionRequestDTO));
    }

    @ApiOperation(value = "分页查询机构产品信息")
    @PostMapping("/pageInstitutionPro")
    public BaseResponse pageInstitutionPro(@RequestBody @ApiParam InstitutionProductDTO institutionProductDTO) {
        return ResultUtil.success(institutionProductChannelService.pageInstitutionPro(institutionProductDTO));
    }

    @ApiOperation(value = "分页查询机构通道信息")
    @PostMapping("/pageInstitutionCha")
    public BaseResponse pageInstitutionCha(@RequestBody @ApiParam InstitutionChannelQueryDTO institutionChannelQueryDTO) {
        return ResultUtil.success(institutionProductChannelService.pageInstitutionCha(institutionChannelQueryDTO));
    }
}
