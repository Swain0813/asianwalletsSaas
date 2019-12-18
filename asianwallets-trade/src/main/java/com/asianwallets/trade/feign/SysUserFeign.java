package com.asianwallets.trade.feign;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.feign.impl.SysUserFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "asianwallets-permissions", fallback = SysUserFeignImpl.class)
public interface SysUserFeign {

    @ApiOperation(value = "检查交易密码")
    @GetMapping("/permission/checkPassword")
    BaseResponse checkPassword(@RequestParam("oldPassword") @ApiParam String oldPassword, @RequestParam("password") @ApiParam String password);
}
