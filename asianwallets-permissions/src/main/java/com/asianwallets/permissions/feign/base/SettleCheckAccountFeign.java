package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.dto.TradeCheckAccountSettleExportDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.permissions.feign.base.impl.SettleCheckAccountFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

/**
 * 结算对账单
 */
@FeignClient(value = "asianwallets-base", fallback = SettleCheckAccountFeignImpl.class)
public interface SettleCheckAccountFeign {


    @ApiOperation(value = "查询前一天所有结算记录")
    @GetMapping("/settleCheckAccount/selectTcsStFlow")
    BaseResponse selectTcsStFlow(@RequestParam("time") @ApiParam String time);

    @ApiOperation(value = "分页查询商户结算对账")
    @PostMapping("/settleCheckAccount/pageSettleAccountCheck")
    BaseResponse pageSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO);

    @ApiOperation(value = "分页查询商户结算对账详情")
    @PostMapping("/settleCheckAccount/pageSettleAccountCheckDetail")
    BaseResponse pageSettleAccountCheckDetail(@RequestBody @ApiParam TradeCheckAccountDTO tradeCheckAccountDTO);

    @ApiOperation(value = "导出商户结算对账单")
    @PostMapping("/settleCheckAccount/exportSettleAccountCheck")
    Map<String, Object> exportSettleAccountCheck(@RequestBody @ApiParam TradeCheckAccountSettleExportDTO tradeCheckAccountDTO);
}
