package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionChannelQueryDTO;
import com.asianwallets.common.dto.InstitutionProductChannelDTO;
import com.asianwallets.common.dto.InstitutionProductDTO;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.InstitutionProductChannelFeign;
import com.asianwallets.permissions.service.OperationLogService;
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

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "新增机构关联产品通道信息")
    @PostMapping("/addInsProCha")
    public BaseResponse addInstitutionProductChannel(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(institutionProductChannelDTOList),
                "新增机构关联产品通道信息"));
        return institutionProductChannelFeign.addInsProCha(institutionProductChannelDTOList);
    }

    @ApiOperation(value = "修改机构关联产品通道信息")
    @PostMapping("/updateInsProCha")
    public BaseResponse updateInsProChaByInsId(@RequestBody @ApiParam List<InstitutionProductChannelDTO> institutionProductChannelDTOList) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(institutionProductChannelDTOList),
                "修改机构关联产品通道信息"));
        return institutionProductChannelFeign.updateInsProCha(institutionProductChannelDTOList);
    }

    @ApiOperation(value = "根据机构ID查询机构关联产品通道信息")
    @GetMapping("/getInsProChaByInsId")
    public BaseResponse getInsProChaByInsId(@RequestParam(required = false) @ApiParam String insId, @RequestParam(required = false) @ApiParam String merId) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(insId),
                "根据机构ID查询机构关联产品通道信息"));
        return institutionProductChannelFeign.getInsProChaByInsId(insId, merId);
    }

    @ApiOperation(value = "查询所有产品关联通道信息(前端用)")
    @PostMapping("/getAllProCha")
    public BaseResponse getAllProCha() {
        return institutionProductChannelFeign.getAllProCha();
    }


    @ApiOperation(value = "分页查询机构参数设置")
    @PostMapping("/pageInstitutionRequests")
    public BaseResponse pageInstitutionRequests(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRequestDTO),
                "分页查询机构参数设置"));
        return institutionProductChannelFeign.pageInstitutionRequests(institutionRequestDTO);
    }

    @ApiOperation(value = "分页查询机构产品信息")
    @PostMapping("/pageInstitutionPro")
    public BaseResponse pageInstitutionPro(@RequestBody @ApiParam InstitutionProductDTO institutionProductDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionProductDTO),
                "分页查询机构产品信息"));
        return institutionProductChannelFeign.pageInstitutionPro(institutionProductDTO);
    }

    @ApiOperation(value = "分页查询机构通道信息")
    @PostMapping("/pageInstitutionCha")
    public BaseResponse pageInstitutionCha(@RequestBody @ApiParam InstitutionChannelQueryDTO institutionChannelQueryDTO) {
        operationLogService.addOperationLog(setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionChannelQueryDTO),
                "分页查询机构通道信息"));
        return institutionProductChannelFeign.pageInstitutionCha(institutionChannelQueryDTO);
    }
}
