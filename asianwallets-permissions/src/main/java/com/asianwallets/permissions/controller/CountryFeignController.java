package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.CountryDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.CountryFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CurrencyController
 * @Description 国家地区
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/country")
@Api("国家地区")
public class CountryFeignController extends BaseController {


    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CountryFeign countryFeign;

    @ApiOperation(value = "新增国家")
    @PostMapping("addCountry")
    public BaseResponse addCountry(@RequestBody @ApiParam CountryDTO country) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(country),
                "新增国家"));
        return ResultUtil.success(countryFeign.addCountry(country));
    }

    @ApiOperation(value = "修改国家")
    @PutMapping("updateCountry")
    public BaseResponse updateCountry(@RequestBody @ApiParam CountryDTO country) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(country),
                "修改国家"));
        return ResultUtil.success(countryFeign.updateCountry(country));
    }

    @ApiOperation(value = "查询国家")
    @PostMapping("pageCountry")
    public BaseResponse pageCountry(@RequestBody @ApiParam CountryDTO country) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(country),
                "查询国家"));
        return ResultUtil.success(countryFeign.pageCountry(country));
    }

    @ApiOperation(value = "启用禁用国家")
    @PutMapping("banCountry")
    public BaseResponse banCountry(@RequestBody @ApiParam CountryDTO country) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(country),
                "启用禁用国家"));
        return ResultUtil.success(countryFeign.banCountry(country));
    }

    @ApiOperation(value = "查询所有国家地区")
    @GetMapping("inquireAllCountry")
    public BaseResponse inquireAllCountry() {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "查询所有国家地区"));
        return ResultUtil.success(countryFeign.inquireAllCountry());
    }
}
