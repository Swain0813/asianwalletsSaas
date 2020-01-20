package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.ExportTradeAccountVO;
import com.asianwallets.permissions.feign.base.impl.TradeCheckAccountFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "asianwallets-base", fallback = TradeCheckAccountFeignImpl.class)
public interface TradeCheckAccountFeign {

    @ApiOperation(value = "分页查询交易对账总表信息")
    @PostMapping("/base/pageFindTradeCheckAccount")
    BaseResponse pageFindTradeCheckAccount(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO);

    @ApiOperation(value = "分页查询交易对账详细表信息")
    @PostMapping("/base/pageFindTradeCheckAccountDetail")
    BaseResponse pageFindTradeCheckAccountDetail(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO);

    @ApiOperation(value = "导出商户交易对账单")
    @PostMapping("/base/exportTradeCheckAccount")
    ExportTradeAccountVO exportTradeCheckAccount(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO);
}
