package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.CountryFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 国家模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = CountryFeignImpl.class)
public interface CountryFeign {
    @ApiOperation(value = "新增国家")
    @PostMapping("/country/addCountry")
    public BaseResponse addCountry(@RequestBody @ApiParam CountryDTO country);

    @ApiOperation(value = "修改国家")
    @PutMapping("/country/updateCountry")
    public BaseResponse updateCountry(@RequestBody @ApiParam CountryDTO country);

    @ApiOperation(value = "查询国家")
    @PostMapping("/country/pageCountry")
    public BaseResponse pageCountry(@RequestBody @ApiParam CountryDTO country);

    @ApiOperation(value = "启用禁用国家")
    @PutMapping("/country/banCountry")
    public BaseResponse banCountry(@RequestBody @ApiParam CountryDTO country);

    @ApiOperation(value = "查询所有国家地区")
    @GetMapping("/country/inquireAllCountry")
    public BaseResponse inquireAllCountry();
}
