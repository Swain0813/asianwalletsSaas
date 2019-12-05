package com.asianwallets.base.controller;

import com.asianwallets.base.service.MccService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName mcc
 * @Description mcc
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/mcc")
@Api("mcc")
public class MccController extends BaseController {

    @Autowired
    private MccService mccService;

    /*ApiOperation(value = "新增国家")
    @PostMapping("addCountry")
    public BaseResponse addCountry(@RequestBody @ApiParam CountryDTO country) {
        if (StringUtils.isBlank(country.getLanguage())) {
            country.setLanguage(this.getLanguage());
        }
        country.setCreator(this.getSysUserVO().getUsername());
        return ResultUtil.success(countryService.addCountry(country));
    }

    @ApiOperation(value = "修改国家")
    @PostMapping("updateCountry")
    public BaseResponse updateCountry(@RequestBody @ApiParam CountryDTO country) {
        country.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(countryService.updateCountry(country));
    }

    @ApiOperation(value = "查询国家")
    @PostMapping("pageCountry")
    public BaseResponse pageCountry(@RequestBody @ApiParam CountryDTO country) {
        if (StringUtils.isBlank(country.getLanguage())) {
            country.setLanguage(this.getLanguage());
        }
        return ResultUtil.success(countryService.pageCountry(country));
    }

    @ApiOperation(value = "启用禁用国家")
    @PostMapping("banCountry")
    public BaseResponse banCountry(@RequestBody @ApiParam CountryDTO country) {
        country.setModifier(this.getSysUserVO().getUsername());
        return ResultUtil.success(countryService.banCountry(country));}*/


    @ApiOperation(value = "查询国家地区")
    @GetMapping("inquireAllMcc")
    public BaseResponse inquireAllMcc() {
        return ResultUtil.success(mccService.inquireAllMcc());
    }
}
