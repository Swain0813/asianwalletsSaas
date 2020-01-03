package com.asianwallets.trade.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.CashierDTO;
import com.asianwallets.common.dto.MockOrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.trade.dto.CalcRateDTO;
import com.asianwallets.trade.dto.OnlineOrderQueryDTO;
import com.asianwallets.trade.service.OnlineGatewayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(description = "线上收银台交易接口")
@RequestMapping("/online")
public class CashierTradeController extends BaseController {

    @Autowired
    private OnlineGatewayService onlineGatewayService;

    @ApiOperation(value = "收银台收单接口")
    @PostMapping("/cashierGateway")
    @CrossOrigin
    public BaseResponse cashierGateway(@RequestBody @ApiParam @Valid CashierDTO cashierDTO) {
        return onlineGatewayService.cashierGateway(cashierDTO);
    }

    @ApiOperation(value = "收银台所需的基础信息")
    @GetMapping("/cashier")
    @CrossOrigin
    public BaseResponse cashier(@RequestParam("orderId") @ApiParam String orderId) {
        return ResultUtil.success(onlineGatewayService.cashier(orderId, this.getLanguage()));
    }

    @ApiOperation(value = "模拟界面所需的基础信息")
    @GetMapping("/simulation")
    @CrossOrigin
    public BaseResponse simulation(@RequestParam("merchantId") @ApiParam String merchantId) {
        return ResultUtil.success(onlineGatewayService.simulation(merchantId, this.getLanguage()));
    }

    @ApiOperation(value = "模拟界面查询订单信息")
    @PostMapping("getByMultipleConditions")
    @CrossOrigin
    public BaseResponse getByMultipleConditions(@RequestBody @ApiParam MockOrdersDTO ordersDTO) {
        return ResultUtil.success(onlineGatewayService.getByMultipleConditions(ordersDTO));
    }

    @ApiOperation(value = "收银台换汇金额计算")
    @PostMapping("calcExchangeRate")
    @CrossOrigin
    public BaseResponse calcCashierExchangeRate(@RequestBody @ApiParam @Valid CalcRateDTO calcRateDTO) {
        return onlineGatewayService.calcCashierExchangeRate(calcRateDTO);
    }

    @ApiOperation(value = "收银台查询订单状态--AD3")
    @PostMapping("/onlineOrderQuerying")
    @CrossOrigin
    public BaseResponse onlineOrderQuerying(@RequestBody @ApiParam @Valid OnlineOrderQueryDTO OnlineOrderQueryDTO) {
        return onlineGatewayService.onlineOrderQuery(OnlineOrderQueryDTO);
    }
}

