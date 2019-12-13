package com.asianwallets.permissions.controller;

import com.alibaba.fastjson.JSON;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.OrdersFeign;
import com.asianwallets.permissions.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(description = "订单接口")
@RequestMapping("/orders")
public class OrdersFeignController extends BaseController {

    @Autowired
    private OrdersFeign ordersFeign;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "分页查询订单信息")
    @PostMapping("pageFindOrders")
    public BaseResponse pageFindOrders(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ordersDTO),
                "分页查询订单信息"));
        return ordersFeign.pageFindOrders(ordersDTO);
    }

    @ApiOperation(value = "查询订单详情信息")
    @GetMapping("getOrdersDetail")
    public BaseResponse getOrdersDetail(@RequestParam @ApiParam String id) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(id),
                "查询订单详情信息"));
        return ordersFeign.getOrdersDetail(id);
    }

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("pageFindOrdersRefund")
    public BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        operationLogService.addOperationLog(setOperationLog(getSysUserVO().getUsername(), AsianWalletConstant.SELECT, JSON.toJSONString(ordersDTO),
                "分页查询退款订单信息"));
        return ordersFeign.pageFindOrders(ordersDTO);
    }
}
