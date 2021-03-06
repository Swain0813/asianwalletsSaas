package com.asianwallets.base.controller;

import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.ExportOrdersVO;
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

    @ApiOperation(value = "分页查询订单信息")
    @PostMapping("pageFindOrders")
    public BaseResponse pageFindOrders(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(ordersService.pageFindOrders(ordersDTO));
    }

    @ApiOperation(value = "查询订单详情信息")
    @GetMapping("getOrdersDetail")
    public BaseResponse getOrdersDetail(@RequestParam("id") @ApiParam String id) {
        return ResultUtil.success(ordersService.getOrdersDetail(id));
    }

    @ApiOperation(value = "订单导出")
    @PostMapping("exportOrders")
    public List<ExportOrdersVO> exportOrders(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ordersService.exportOrders(ordersDTO);
    }

    @ApiOperation(value = "交易统计")
    @PostMapping("statistics")
    public BaseResponse statistics(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(ordersService.statistics(ordersDTO));
    }

    @ApiOperation(value = "产品交易统计")
    @PostMapping("productStatistics")
    public BaseResponse productStatistics(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(ordersService.productStatistics(ordersDTO));
    }
}
