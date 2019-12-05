package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.MccFeignImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 国家模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = MccFeignImpl.class)
public interface MccFeign {

    @ApiOperation(value = "查询所有国家地区")
    @GetMapping("/mcc/inquireAllMcc")
    BaseResponse inquireAllMcc();
}
