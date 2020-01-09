package com.asianwallets.permissions.feign.base;

import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.AccountSearchExportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.*;
import com.asianwallets.permissions.feign.base.impl.AccountFeignImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 账户信息相关
 */
@FeignClient(value = "asianwallets-base", fallback = AccountFeignImpl.class)
public interface AccountFeign {

    @ApiOperation(value = "分页查询账户信息")
    @PostMapping("/account/pageFindAccount")
    BaseResponse pageFindAccount(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "导出账户信息")
    @PostMapping("/account/exportAccountList")
    List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO);

    @ApiOperation(value = "查询清算户余额流水详情")
    @PostMapping("/account/pageClearLogs")
    BaseResponse pageClearLogs(@RequestBody @ApiParam AccountSearchDTO clearSearchDTO);

    @ApiOperation(value = "导出清算户余额流水详情")
    @PostMapping("/account/exportClearLogs")
    List<TmMerChTvAcctBalanceVO> exportClearLogs(@RequestBody @ApiParam AccountSearchExportDTO clearSearchDTO);

    @ApiOperation(value = "查询结算户余额流水详情")
    @PostMapping("/account/pageSettleLogs")
    BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "导出结算户余额流水详情")
    @PostMapping("/account/exportSettleLogs")
    List<TmMerChTvAcctBalanceVO> exportSettleLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO);

    @ApiOperation(value = "查询冻结余额流水详情")
    @PostMapping("/account/pageFrozenLogs")
    BaseResponse pageFrozenLogs(@RequestBody @ApiParam AccountSearchDTO frozenMarginInfoDTO);

    @ApiOperation(value = "导出冻结余额流水详情")
    @PostMapping("/account/exportFrozenLogs")
    List<TmMerChTvAcctBalanceVO> exportFrozenLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO);

    @ApiOperation(value = "导出商户余额")
    @PostMapping("/account/exportMerchantBalance")
    List<MerchantBalanceVO> exportMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO);

    @ApiOperation(value = "分页查询商户余额")
    @PostMapping("/account/pageFindMerchantBalance")
    BaseResponse pageFindMerchantBalance(OrdersDTO ordersDTO);
}
