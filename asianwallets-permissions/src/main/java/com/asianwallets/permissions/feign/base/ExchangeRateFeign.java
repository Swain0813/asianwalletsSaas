package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.ExchangeRateFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 汇率接口Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = ExchangeRateFeignImpl.class)
public interface ExchangeRateFeign {

    @ApiOperation(value = "添加汇率信息")
    @PostMapping("/exchangeRate/addExchangeRate")
    BaseResponse addExchangeRate(@RequestBody @ApiParam ExchangeRateDTO exchangeRateDTO);

    @ApiOperation(value = "禁用汇率信息")
    @GetMapping("/exchangeRate/banExchangeRate")
    BaseResponse banExchangeRate(@RequestParam("id") @ApiParam String id);


    @ApiOperation(value = "分页查询汇率信息")
    @PostMapping("/exchangeRate/getByMultipleConditions")
    BaseResponse getByMultipleConditions(@RequestBody @ApiParam ExchangeRateDTO exchangeRateDTO);


}
