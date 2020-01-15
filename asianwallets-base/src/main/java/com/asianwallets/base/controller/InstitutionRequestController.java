package com.asianwallets.base.controller;
import com.asianwallets.base.service.InstitutionRequestService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
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
public class InstitutionRequestController extends BaseController {

    @Autowired
    private InstitutionRequestService institutionRequestService;

    @ApiOperation(value = "添加机构请求参数设置")
    @PostMapping("/addInstitutionRequest")
    public BaseResponse addInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO) {
        return ResultUtil.success(institutionRequestService.addInstitutionRequest(this.getSysUserVO().getUsername(), institutionRequestDTO));
    }

    @ApiOperation(value = "分页查询机构请求参数设置")
    @PostMapping("/pageInstitutionRequest")
    public BaseResponse pageInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO) {
        return ResultUtil.success(institutionRequestService.pageInstitutionRequest(institutionRequestDTO));
    }


    @ApiOperation(value = "根据机构编号查询机构请求参数设置的详情")
    @PostMapping(value = "/getInstitutionRequest")
    public BaseResponse getInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO) {
        return ResultUtil.success(institutionRequestService.getInstitutionRequests(institutionRequestDTO));
    }

    @ApiOperation(value = "修改机构请求参数设置")
    @PostMapping("/updateInstitutionRequest")
    public BaseResponse updateInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO) {
        return ResultUtil.success(institutionRequestService.updateInstitutionRequest(this.getSysUserVO().getUsername(), institutionRequestDTO));
    }

}
