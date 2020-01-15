package com.asianwallets.base.controller;

import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.*;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 账户信息相关
 */
@RestController
@Api(description = "账户管理接口")
@RequestMapping("/account")
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;


    @ApiOperation(value = "分页查询账户信息")
    @PostMapping("/pageFindAccount")
    public BaseResponse pageFindAccount(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return ResultUtil.success(accountService.pageFindAccount(accountSearchDTO));
    }

    @ApiOperation(value = "导出账户信息")
    @PostMapping("/exportAccountList")
    public List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO) {
        return accountService.exportAccountList(accountSearchDTO);
    }

    @ApiOperation(value = "查询清算户余额流水详情")
    @PostMapping("/pageClearLogs")
    public BaseResponse pageClearLogs(@RequestBody @ApiParam AccountSearchDTO clearSearchDTO) {
        return ResultUtil.success(accountService.pageClearLogs(clearSearchDTO));
    }

    @ApiOperation(value = "导出清算户余额流水详情")
    @PostMapping("/exportClearLogs")
    public List<TmMerChTvAcctBalanceVO> exportClearLogs(@RequestBody @ApiParam AccountSearchExportDTO clearSearchDTO) {
        return accountService.exportClearLogs(clearSearchDTO);
    }

    @ApiOperation(value = "查询结算户余额流水详情")
    @PostMapping("/pageSettleLogs")
    public BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return ResultUtil.success(accountService.pageSettleLogs(accountSearchDTO));
    }

    @ApiOperation(value = "导出结算户余额流水详情")
    @PostMapping("/exportSettleLogs")
    public List<TmMerChTvAcctBalanceVO> exportSettleLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO) {
        return accountService.exportSettleLogs(accountSearchDTO);
    }

    @ApiOperation(value = "查询冻结余额流水详情")
    @PostMapping("/pageFrozenLogs")
    public BaseResponse pageFrozenLogs(@RequestBody @ApiParam AccountSearchDTO frozenMarginInfoDTO) {
        return ResultUtil.success(accountService.pageFrozenLogs(frozenMarginInfoDTO));
    }

    @ApiOperation(value = "导出冻结余额流水详情")
    @PostMapping("/exportFrozenLogs")
    public List<TmMerChTvAcctBalanceVO> exportFrozenLogs(@RequestBody @ApiParam AccountSearchExportDTO accountSearchDTO) {
        return accountService.exportFrozenLogs(accountSearchDTO);
    }

    /**
     * saas后台管理系统用
     * @param ordersDTO
     * @return
     */
    @ApiOperation(value = "分页查询商户余额")
    @PostMapping("/pageFindMerchantBalance")
    public BaseResponse pageMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(accountService.pageFindMerchantBalance(ordersDTO));
    }

    /**
     * saas后台管理系统用
     * @param ordersDTO
     * @return
     */
    @ApiOperation(value = "导出商户余额")
    @PostMapping("/exportMerchantBalance")
    public List<MerchantBalanceVO> exportMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return accountService.exportMerchantBalance(ordersDTO);
    }
}
