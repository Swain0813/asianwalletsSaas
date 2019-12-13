package com.asianwallets.base.controller;

import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "订单接口")
@RequestMapping("/orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrdersService ordersService;

    //-----------------------------收单

    @ApiOperation(value = "分页查询订单信息")
    @PostMapping("pageFindOrders")
    public BaseResponse pageFindOrders(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(ordersService.pageFindOrders(ordersDTO));
    }

    @ApiOperation(value = "查询订单详情信息")
    @GetMapping("getOrdersDetail")
    public BaseResponse getOrdersDetail(@RequestParam @ApiParam String id) {
        return ResultUtil.success(ordersService.getOrdersDetail(id));
    }

    @ApiOperation(value = "订单导出")
    @PostMapping("exportOrders")
    public List<ExportOrdersVO> exportOrders(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ordersService.exportOrders(ordersDTO);
    }

    //-----------------------------退款

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("pageFindOrdersRefund")
    public BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO) {
        return ResultUtil.success(ordersService.pageFindOrdersRefund(ordersRefundDTO));
    }

    @ApiOperation(value = "查询退款订单详情信息")
    @GetMapping("getOrdersRefundDetail")
    public BaseResponse getOrdersRefundDetail(@RequestParam @ApiParam String refundId) {
        return ResultUtil.success(ordersService.getOrdersRefundDetail(refundId));
    }

    @ApiOperation(value = "退款单导出")
    @PostMapping("exportOrdersRefund")
    public List<OrdersRefundVO> exportOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO) {
        return ordersService.exportOrdersRefund(ordersRefundDTO);
    }
}
