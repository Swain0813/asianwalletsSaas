package com.asianwallets.permissions.controller;
import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ExchangeRateDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.ExchangeRateFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 汇率管理接口
 */
@RestController
@Api(description = "汇率管理接口")
@RequestMapping("/exchangeRate")
public class ExchangeRateFeignController extends BaseController {

    @Autowired
    private ExchangeRateFeign exchangeRateFeign;

    @Autowired
    private OperationLogService operationLogService;


    @ApiOperation(value = "添加汇率信息")
    @PostMapping("addExchangeRate")
    public BaseResponse addExchangeRate(@RequestBody @ApiParam ExchangeRateDTO exchangeRateDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.ADD, JSON.toJSONString(exchangeRateDTO),
                "添加汇率信息"));
        return exchangeRateFeign.addExchangeRate(exchangeRateDTO);
    }

    @ApiOperation(value = "禁用汇率信息")
    @GetMapping("banExchangeRate")
    public BaseResponse banExchangeRate(@RequestParam @ApiParam String id){
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.UPDATE, JSON.toJSONString(id),
                "禁用汇率信息"));
        return exchangeRateFeign.banExchangeRate(id);
    }


    @ApiOperation(value = "分页查询汇率信息")
    @PostMapping("getByMultipleConditions")
    public BaseResponse getByMultipleConditions(@RequestBody @ApiParam ExchangeRateDTO exchangeRateDTO) {
        //添加操作日志
        operationLogService.addOperationLog(this.setOperationLog(this.getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(exchangeRateDTO),
                "分页查询汇率信息"));
        return exchangeRateFeign.getByMultipleConditions(exchangeRateDTO);
    }

}
