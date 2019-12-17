package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.asianwallets.permissions.feign.base.impl.OrdersRefundFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "asianwallets-base", fallback = OrdersRefundFeignImpl.class)
public interface OrdersRefundFeign {

    @ApiOperation(value = "分页查询退款订单信息")
    @PostMapping("/ordersRefund/pageFindOrdersRefund")
    BaseResponse pageFindOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO);

    @ApiOperation(value = "查询退款订单详情信息")
    @GetMapping("/ordersRefund/getOrdersRefundDetail")
    BaseResponse getOrdersRefundDetail(@RequestParam("refundId") @ApiParam String refundId);

    @ApiOperation(value = "导出退款订单")
    @PostMapping("/ordersRefund/exportOrdersRefund")
    List<OrdersRefundVO> exportOrdersRefund(@RequestBody @ApiParam OrdersRefundDTO ordersRefundDTO);
}
