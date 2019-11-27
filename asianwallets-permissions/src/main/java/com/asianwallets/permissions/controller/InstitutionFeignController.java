package com.asianwallets.permissions.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.InstitutionFeign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: YangXu
 * @create: 2019-11-27 10:02
 **/
@RestController
@Api(description = "商户管理接口")
@RequestMapping("/institution")
public class InstitutionFeignController extends BaseController {

    @Autowired
    private InstitutionFeign institutionFeign;


    @ApiOperation(value = "添加机构")
    @PostMapping("addInstitution")
    public BaseResponse addInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return institutionFeign.addInstitution(institutionDTO);
    }


    @ApiOperation(value = "修改机构")
    @PostMapping("updateInstitution")
    public BaseResponse updateInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return institutionFeign.updateInstitution(institutionDTO);
    }

    @ApiOperation(value = "分页查询机构信息列表")
    @PostMapping("/pageFindInstitution")
    public BaseResponse pageFindInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return institutionFeign.pageFindInstitution(institutionDTO);
    }

    @ApiOperation(value = "分页查询机构审核信息列表")
    @PostMapping("/pageFindInstitutionAudit")
    public BaseResponse pageFindInstitutionAudit(@RequestBody @ApiParam InstitutionDTO institutionDTO) {
        return institutionFeign.pageFindInstitutionAudit(institutionDTO);
    }

    @ApiOperation(value = "根据机构Id查询机构信息详情")
    @GetMapping("/getInstitutionInfo")
    public BaseResponse getInstitutionInfo(@RequestParam @ApiParam String id) {
        return institutionFeign.getInstitutionInfo(id);
    }


    @ApiOperation(value = "根据机构Id查询机构审核信息详情")
    @GetMapping("/getInstitutionInfoAudit")
    public BaseResponse getInstitutionInfoAudit(String id) {
        return institutionFeign.getInstitutionInfoAudit(id);
    }

    @ApiOperation(value = "审核机构信息接口")
    @GetMapping("/auditInstitution")
    public BaseResponse auditInstitution(@RequestParam @ApiParam String institutionId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark) {
        return institutionFeign.auditInstitution(institutionId, enabled, remark);
    }

}
