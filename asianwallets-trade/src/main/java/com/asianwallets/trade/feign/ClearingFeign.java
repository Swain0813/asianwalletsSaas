package com.asianwallets.trade.feign;

import com.asianwallets.common.vo.clearing.CSFrozenFundsRequest;
import com.asianwallets.common.vo.clearing.IntoAndOutMerhtAccountRequest;
import com.asianwallets.trade.feign.Impl.ClearingFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-clearing", fallback = ClearingFeignImpl.class)
public interface ClearingFeign {

    @ApiOperation(value = "资金变动接口")
    @PostMapping("/IntoAccountAction/IntoAndOutMerhtAccount")
    IntoAndOutMerhtAccountRequest intoAndOutMerhtAccount(@RequestBody @ApiParam IntoAndOutMerhtAccountRequest intoAndOutMerhtAccountRequest);


    @ApiOperation(value = "资金冻结/解冻接口")
    @PostMapping("/FrozenFundsAction/CSFrozenFunds")
    CSFrozenFundsRequest CSFrozenFunds(@RequestBody @ApiParam CSFrozenFundsRequest ffr);

}
