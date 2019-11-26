package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.HolidaysDTO;
import com.asianwallets.common.dto.InstitutionDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.InstitutionFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * InstitutionFeignImpl
 */
@FeignClient(value = "asianwallets-base", fallback = InstitutionFeignImpl.class)
public interface InstitutionFeign {

    @ApiOperation(value = "添加机构")
    @PostMapping("/institution/addInstitution")
    BaseResponse addInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO);

    @ApiOperation(value = "修改机构")
    @PostMapping("/institution/updateInstitution")
    BaseResponse updateInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO);

    @ApiOperation(value = "分页查询机构信息列表")
    @PostMapping("/institution/pageFindInstitution")
    BaseResponse pageFindInstitution(@RequestBody @ApiParam InstitutionDTO institutionDTO);

    @ApiOperation(value = "分页查询机构审核信息列表")
    @PostMapping("/institution/pageFindInstitutionAudit")
    BaseResponse pageFindInstitutionAudit(@RequestBody @ApiParam InstitutionDTO institutionDTO);

    @ApiOperation(value = "根据机构Id查询机构信息详情")
    @GetMapping("/institution/getInstitutionInfo")
    BaseResponse getInstitutionInfo(@RequestParam @ApiParam String id);

    @ApiOperation(value = "根据机构Id查询机构审核信息详情")
    @GetMapping("/institution/getInstitutionInfoAudit")
    BaseResponse getInstitutionInfoAudit(String id);

    @ApiOperation(value = "审核机构信息接口")
    @GetMapping("/institution/auditInstitution")
    BaseResponse auditInstitution(@RequestParam @ApiParam String institutionId, @RequestParam @ApiParam Boolean enabled, @RequestParam(required = false) @ApiParam String remark);
}
