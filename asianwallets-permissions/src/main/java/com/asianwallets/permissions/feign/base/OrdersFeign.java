package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.ArtificialDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.asianwallets.permissions.feign.base.impl.OrdersFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "asianwallets-base", fallback = OrdersFeignImpl.class)
public interface OrdersFeign {

    @ApiOperation(value = "分页查询订单信息")
    @PostMapping("/orders/pageFindOrders")
    BaseResponse pageFindOrders(@RequestBody @ApiParam OrdersDTO ordersDTO);

    @ApiOperation(value = "查询订单详情信息")
    @GetMapping("/orders/getOrdersDetail")
    BaseResponse getOrdersDetail(@RequestParam("id") @ApiParam String id);

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("/orders/pageFindOrdersRefund")
    BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO);

    @ApiOperation(value = "查询退款订单详情信息")
    @GetMapping("/orders/getOrdersRefundDetail")
    BaseResponse getOrdersRefundDetail(@RequestParam("refundId") @ApiParam String refundId);

    @ApiOperation(value = "导出退款订单")
    @PostMapping("/orders/exportOrdersRefund")
    List<OrdersRefundVO> exportOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO);

    @ApiOperation(value = "订单导出")
    @PostMapping("/orders/exportOrders")
    List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO);

    @ApiOperation(value = "运营后台修改订单状态")
    @PostMapping("/offlineCallback/artificialCallback")
    BaseResponse updateOrderStatus(ArtificialDTO artificialDTO);
}
