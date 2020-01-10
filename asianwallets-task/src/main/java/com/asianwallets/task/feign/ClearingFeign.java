package com.asianwallets.task.feign;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.task.feign.Impl.ClearingFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-clearing", fallback = ClearingFeignImpl.class)
public interface ClearingFeign {

    @ApiOperation(value = "资金冻结/解冻接口")
    @PostMapping("/FrozenFundsAction/CSFrozenFunds")
    FinancialFreezeDTO CSFrozenFunds(@RequestBody @ApiParam FinancialFreezeDTO ffr);

}
