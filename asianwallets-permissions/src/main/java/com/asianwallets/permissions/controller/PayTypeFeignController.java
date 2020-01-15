package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.PayTypeDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.permissions.feign.base.PayTypeFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName 支付方式
 * @Description 支付方式
 * @Author abc
 * @Date 2019/11/22 6:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/paytype")
@Api(description = "支付方式接口")
public class PayTypeFeignController extends BaseController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private PayTypeFeign payTypeFeign;

    @ApiOperation(value = "新增支付方式")
    @PostMapping("addPayType")
    public BaseResponse addPayType(@RequestBody @ApiParam PayTypeDTO payTypeDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.ADD, JSON.toJSONString(payTypeDTO),
                "新增支付方式"));
        return ResultUtil.success(payTypeFeign.addPaytype(payTypeDTO));
    }

    @ApiOperation(value = "修改支付方式")
    @PostMapping("updatePayType")
    public BaseResponse updateCurrency(@RequestBody @ApiParam PayTypeDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "修改支付方式"));
        return ResultUtil.success(payTypeFeign.updatePaytype(currencyDTO));
    }

    @ApiOperation(value = "查询支付方式")
    @PostMapping("pagePayType")
    public BaseResponse pagePayType(@RequestBody @ApiParam PayTypeDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, JSON.toJSONString(currencyDTO),
                "查询支付方式"));
        return ResultUtil.success(payTypeFeign.pagePaytype(currencyDTO));
    }

    @ApiOperation(value = "启用禁用支付方式")
    @PostMapping("banPayType")
    public BaseResponse banDeviceVendor(@RequestBody @ApiParam PayTypeDTO currencyDTO) {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.UPDATE, JSON.toJSONString(currencyDTO),
                "启用禁用支付方式"));
        return ResultUtil.success(payTypeFeign.banCurrency(currencyDTO));
    }

    @ApiOperation(value = "查询所有支付方式")
    @GetMapping("inquireAllPayType")
    public BaseResponse inquireAllPayType() {
        operationLogService.addOperationLog(this.setOperationLog(this.getUserName(), AsianWalletConstant.SELECT, null,
                "查询所有支付方式"));
        return ResultUtil.success(payTypeFeign.inquireAllPaytype());
    }
}
