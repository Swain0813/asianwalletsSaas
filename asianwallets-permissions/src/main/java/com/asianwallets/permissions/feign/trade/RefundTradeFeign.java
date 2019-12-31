package com.asianwallets.permissions.feign.trade;

import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.trade.impl.RefundTradeFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 退款feign相关模块
 */
@FeignClient(value = "asianwallets-trade", fallback = RefundTradeFeignImpl.class)
public interface RefundTradeFeign {

    @ApiOperation(value = "人工退款接口")
    @GetMapping(value = "/trade/artificialRefund")
    BaseResponse artificialRefund(@RequestParam("refundOrderId") @ApiParam String refundOrderId, @RequestParam("enabled") Boolean enabled, @RequestParam("remark") String remark);
}
