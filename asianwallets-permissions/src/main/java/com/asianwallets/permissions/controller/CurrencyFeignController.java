package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.CurrencyDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.CurrencyFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CurrencyController
 * @Description 币种
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/currency")
@Api("币种管理")
public class CurrencyFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CurrencyFeign currencyFeign;

    @ApiOperation(value = "新增币种")
    @PostMapping("addCurrency")
    public BaseResponse addCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(currencyDTO),
                "新增币种"));
        return ResultUtil.success(currencyFeign.addCurrency(currencyDTO));
    }

    @ApiOperation(value = "修改币种")
    @PutMapping("updateCurrency")
    public BaseResponse updateCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "修改币种"));
        return ResultUtil.success(currencyFeign.updateCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询币种")
    @PostMapping("pageCurrency")
    public BaseResponse pageCurrency(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(currencyDTO),
                "查询币种"));
        return ResultUtil.success(currencyFeign.pageCurrency(currencyDTO));
    }

    @ApiOperation(value = "启用禁用币种")
    @PutMapping("banCurrency")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam CurrencyDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "启用禁用币种"));
        return ResultUtil.success(currencyFeign.banCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询所有币种")
    @GetMapping("inquireAllCurrency")
    public BaseResponse inquireAllCurrency() {
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, null,
                "查询所有币种"));
        return ResultUtil.success(currencyFeign.inquireAllCurrency());
    }
}
