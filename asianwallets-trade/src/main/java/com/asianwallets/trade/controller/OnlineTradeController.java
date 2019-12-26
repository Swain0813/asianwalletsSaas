package com.asianwallets.trade.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.trade.dto.OnlineTradeDTO;
import com.asianwallets.trade.service.OnlineGatewayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "线上交易接口")
@RequestMapping("/online")
public class OnlineTradeController extends BaseController {

    @Autowired
    private OnlineGatewayService onlineGatewayService;

    @ApiOperation(value = "商户请求收单")
    @PostMapping("/gateway")
    @CrossOrigin
    public BaseResponse gateway(OnlineTradeDTO onlineTradeDTO) {
        return onlineGatewayService.gateway(onlineTradeDTO);
    }
}

