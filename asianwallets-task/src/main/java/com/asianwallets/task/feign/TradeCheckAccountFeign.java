package com.asianwallets.task.feign;

import com.asianwallets.task.feign.Impl.TradeCheckAccountFeignImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(value = "asianwallets-base", fallback = TradeCheckAccountFeignImpl.class)
public interface TradeCheckAccountFeign {

    @ApiOperation(value = "生成昨日商户交易对账单")
    @PostMapping("/base/tradeCheckAccount")
    void tradeCheckAccount();
}
