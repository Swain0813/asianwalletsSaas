package com.asianwallets.channels.controller;

import com.asianwallets.channels.service.AliPayService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.alipay.*;
import com.asianwallets.common.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @description: AliPay
 * @author: XuWenQi
 * @create: 2019-06-14 15:14
 **/
@RestController
@Api(description = "AliPay")
@RequestMapping("/aliPay")
public class AliPayController extends BaseController {

    @Autowired
    private AliPayService aliPayService;

    @ApiOperation("支付宝线下BSC接口")
    @PostMapping("aliPayOfflineBSC")
    public BaseResponse aliPayOfflineBSC(@RequestBody @ApiParam @Valid AliPayOfflineBSCDTO aliPayOfflineBSCDTO) {
        return aliPayService.aliPayOfflineBSC(aliPayOfflineBSCDTO);
    }

    @ApiOperation(value = "支付宝CSB接口")
    @PostMapping("aliPayCSB")
    public BaseResponse aliPayCSB(@RequestBody @ApiParam @Valid AliPayCSBDTO aliPayCSBDTO) {
        return aliPayService.aliPayCSB(aliPayCSBDTO);
    }

    @ApiOperation(value = "支付宝CBAlipayWebsite接口")
    @PostMapping("aliPayWebsite")
    public BaseResponse aliPayWebsite(@RequestBody @ApiParam @Valid AliPayWebDTO aliPayWebDTO) {
        return aliPayService.aliPayWebsite(aliPayWebDTO);
    }

    @ApiOperation(value = "支付宝退款接口")
    @PostMapping("alipayRefund")
    public BaseResponse alipayRefund(@RequestBody @ApiParam @Valid AliPayRefundDTO aliPayRefundDTO) {
        return aliPayService.aliPayRefund(aliPayRefundDTO);
    }

    @ApiOperation(value = "支付宝撤销接口")
    @PostMapping("alipayCancel")
    public BaseResponse alipayCancel(@RequestBody @ApiParam @Valid AliPayCancelDTO aliPayCancelDTO) {
        return aliPayService.alipayCancel(aliPayCancelDTO);
    }

    @ApiOperation(value = "支付宝查询接口")
    @PostMapping("alipayQuery")
    public BaseResponse alipayQuery(@RequestBody @ApiParam @Valid AliPayQueryDTO aliPayQueryDTO) {
        return aliPayService.aliPayQuery(aliPayQueryDTO);
    }

    @ApiOperation(value = "支付宝码牌")
    @PostMapping("alipayCard")
    public BaseResponse alipayCard(@RequestBody @ApiParam @Valid AliPayCardDTO aliPayCardDTO) {
        return aliPayService.alipayCard(aliPayCardDTO);
    }


}
