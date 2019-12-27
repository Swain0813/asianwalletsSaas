package com.asianwallets.base.feign;
import com.asianwallets.base.feign.impl.ClearingFeignImpl;
import com.asianwallets.common.vo.clearing.FinancialFreezeDTO;
import com.asianwallets.common.vo.clearing.FundChangeDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-clearing", fallback = ClearingFeignImpl.class)
public interface ClearingFeign {

    @ApiOperation(value = "资金变动接口")
    @PostMapping("/IntoAccountAction/IntoAndOutMerhtAccount")
    FundChangeDTO intoAndOutMerhtAccount(@RequestBody @ApiParam FundChangeDTO fundChangeDTO);


    @ApiOperation(value = "资金冻结/解冻接口")
    @PostMapping("/FrozenFundsAction/CSFrozenFunds")
    FinancialFreezeDTO CSFrozenFunds(@RequestBody @ApiParam FinancialFreezeDTO ffr);

}
