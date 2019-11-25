package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.InstitutionFeignImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 机构接口Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = InstitutionFeignImpl.class)
public interface InstitutionFeign {

    @ApiOperation(value = "根据机构ID查询机构信息")
    @GetMapping("/institution/getInstitutionInfo")
    BaseResponse getInstitutionInfoById(@RequestParam("id") String id);

}
