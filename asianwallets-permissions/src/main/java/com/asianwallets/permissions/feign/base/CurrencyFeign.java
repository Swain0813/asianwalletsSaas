package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.CurrencyFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 国家模块Feign端
 */
@FeignClient(value = "asianwallets-base", fallback = CurrencyFeignImpl.class)
public interface CurrencyFeign {


    @ApiOperation(value = "新增币种")
    @PostMapping("/currency/addCurrency")
    BaseResponse addCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO);

    @ApiOperation(value = "修改币种")
    @PostMapping("/currency/updateCurrency")
    BaseResponse updateCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO);

    @ApiOperation(value = "查询币种")
    @PostMapping("/currency/pageCurrency")
    BaseResponse pageCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO);

    @ApiOperation(value = "启用禁用币种")
    @PostMapping("/currency/banCurrency")
    BaseResponse banCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO);

    @ApiOperation(value = "查询所有币种")
    @GetMapping("/currency/inquireAllCurrency")
    BaseResponse inquireAllCurrency();
}
