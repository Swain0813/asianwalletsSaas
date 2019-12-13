package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.ExchangeRateFeignImpl;
import com.asianwallets.permissions.feign.base.impl.OrdersFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "asianwallets-base", fallback = OrdersFeignImpl.class)
public interface OrdersFeign {

    @ApiOperation(value = "分页查询订单信息")
    @PostMapping("/orders/pageFindOrders")
    BaseResponse pageFindOrders(@RequestBody @ApiParam OrdersDTO ordersDTO);

    @ApiOperation(value = "查询订单详情信息")
    @GetMapping("/orders/getOrdersDetail")
    BaseResponse getOrdersDetail(@RequestParam @ApiParam String id);

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("/orders/pageFindOrdersRefund")
    BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersDTO ordersDTO);

}
