package com.asianwallets.trade.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.trade.dto.OnlineCheckOrdersDTO;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.OnlineGatewayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(description = "线上交易接口")
@RequestMapping("/online")
public class OnlineTradeController extends BaseController {

    @Autowired
    private OnlineGatewayService onlineGatewayService;

    @ApiOperation(value = "商户请求收单")
    @PostMapping("/gateway")
    @CrossOrigin
    public BaseResponse gateway(@RequestBody @Valid OnlineTradeDTO onlineTradeDTO) {
        return onlineGatewayService.gateway(onlineTradeDTO);
    }

    @ApiOperation(value = "线下查询订单列表【对外API】")
    @PostMapping("checkOrder")
    public BaseResponse checkOrder(@RequestBody @ApiParam @Valid OnlineCheckOrdersDTO onlineCheckOrdersDTO) {
        return ResultUtil.success(onlineGatewayService.checkOrder(onlineCheckOrdersDTO));
    }
}

