package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.InstitutionRequestDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.InstitutionRequestFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 机构请求参数设置的Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = InstitutionRequestFeignImpl.class)
public interface InstitutionRequestFeign {

    @ApiOperation(value = "添加机构请求参数设置")
    @PostMapping("/insreqps/addInstitutionRequest")
    BaseResponse addInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO);

    @ApiOperation(value = "分页查询机构请求参数设置")
    @PostMapping("/insreqps/pageInstitutionRequest")
    BaseResponse pageInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO);

    @ApiOperation(value = "根据机构编号查询机构请求参数设置的详情")
    @PostMapping(value = "/insreqps/getInstitutionRequest")
    BaseResponse getInstitutionRequest(@RequestBody @ApiParam InstitutionRequestDTO institutionRequestDTO);


    @ApiOperation(value = "修改机构请求参数设置")
    @PostMapping("/insreqps/updateInstitutionRequest")
    BaseResponse updateInstitutionRequest(@RequestBody @ApiParam List<InstitutionRequestDTO> institutionRequestDTO);
}
