package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.InstitutionRequestFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 机构请求参数设置
 */
@RestController
@Api(description = "机构请求参数设置接口")
@RequestMapping("/insreqps")
public class InstitutionRequestFeignController extends BaseController {

    @Autowired
    private InstitutionRequestFeign institutionRequestFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "添加机构请求参数设置")
    @PostMapping("/addInstitutionRequest")
    public BaseResponse addInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(institutionRequestDTO),
                "添加机构请求参数设置"));
        return institutionRequestFeign.addInstitutionRequest(institutionRequestDTO);
    }


    @ApiOperation(value = "分页查询机构请求参数设置")
    @PostMapping("/pageInstitutionRequest")
    public BaseResponse pageInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRequestDTO),
                "分页查询机构请求参数设置"));
        return institutionRequestFeign.pageInstitutionRequest(institutionRequestDTO);
    }

    @ApiOperation(value = "根据机构编号查询机构请求参数设置的详情")
    @PostMapping(value = "/getInstitutionRequest")
    public BaseResponse getInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(institutionRequestDTO),
                "根据机构编号查询机构请求参数设置的详情"));
        return institutionRequestFeign.getInstitutionRequest(institutionRequestDTO);
    }


    @ApiOperation(value = "修改机构请求参数设置")
    @PostMapping("/updateInstitutionRequest")
    public BaseResponse updateInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(institutionRequestDTO),
                "修改机构请求参数设置"));
        return institutionRequestFeign.updateInstitutionRequest(institutionRequestDTO);
    }
}
