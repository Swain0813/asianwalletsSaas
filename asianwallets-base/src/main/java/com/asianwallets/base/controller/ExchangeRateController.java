package com.asianwallets.base.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.base.service.ExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 汇率接口
 */
@RestController
@Api(description = "汇率接口")
@RequestMapping("/exchangeRate")
public class ExchangeRateController extends BaseController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @ApiOperation(value = "分页多条件查询汇率信息")
    @PostMapping("getByMultipleConditions")
    public BaseResponse getByMultipleConditions(@RequestBody @ApiParam ExchangeRateDTO exchangeRateDTO) {
        return ResultUtil.success(exchangeRateService.getByMultipleConditions(exchangeRateDTO));
    }

    @ApiOperation(value = "添加汇率信息")
    @PostMapping("addExchangeRate")
    public BaseResponse addExchangeRate(@RequestBody @ApiParam @Valid ExchangeRateDTO exchangeRateDTO) {
        return ResultUtil.success(exchangeRateService.addExchangeRate(exchangeRateDTO, this.getUserName().getUsername()));
    }

    @ApiOperation(value = "禁用汇率信息")
    @GetMapping("banExchangeRate")
    public BaseResponse banExchangeRate(@RequestParam @ApiParam String id) {
        return ResultUtil.success(exchangeRateService.banExchangeRate(id, this.getUserName().getUsername()));
    }


}


