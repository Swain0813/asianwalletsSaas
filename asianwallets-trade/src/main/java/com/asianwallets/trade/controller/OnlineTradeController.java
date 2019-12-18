package com.asianwallets.trade.controller;

import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
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
        return ResultUtil.success(onlineGatewayService.gateway(onlineTradeDTO));
    }

   /* @ApiOperation(value = "模拟商户请求收单")
    @PostMapping("/imitateGateway")
    @CrossOrigin
    public BaseResponse imitateGateway(@RequestBody @ApiParam @Valid PlaceOrdersDTO placeOrdersDTO) {
        BaseResponse baseResponse = onlineGatewayService.imitateGateway(placeOrdersDTO);
        if (StringUtils.isEmpty(baseResponse.getCode())) {
            baseResponse.setCode(EResultEnum.SUCCESS.getCode());
            baseResponse.setMsg("SUCCESS");
            return baseResponse;
        } else {
            //code不为空 msg不为空 返回通道的错误信息
            if (!StringUtils.isEmpty(baseResponse.getMsg())) {
                return baseResponse;
            }
            return ResultUtil.error(baseResponse.getCode(), this.getErrorMsgMap(baseResponse.getCode()));
        }
    }*/

    /*@ApiOperation(value = "收银台所需的基础信息")
    @GetMapping("/cashier")
    @CrossOrigin
    public BaseResponse cashier(@RequestParam("orderId") @ApiParam String orderId) {
        return ResultUtil.success(onlineGatewayService.cashier(orderId, this.getLanguage()));
    }

    @ApiOperation(value = "收银台收单接口")
    @PostMapping("/cashierGateway")
    @CrossOrigin
    public BaseResponse cashierGateway(@RequestBody @ApiParam @Valid CashierDTO cashierDTO) {
        BaseResponse baseResponse = onlineGatewayService.cashierGateway(cashierDTO);
        if (StringUtils.isEmpty(baseResponse.getCode())) {
            baseResponse.setCode(EResultEnum.SUCCESS.getCode());
            baseResponse.setMsg("SUCCESS");
            return baseResponse;
        } else {
            return ResultUtil.error(baseResponse.getCode(), this.getErrorMsgMap(baseResponse.getCode()));
        }
    }*/

}

