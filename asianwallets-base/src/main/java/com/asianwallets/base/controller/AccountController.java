package com.asianwallets.base.controller;

import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseController;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.dto.FrozenMarginInfoDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.ResultUtil;
import com.asianwallets.common.vo.AccountListVO;
import com.asianwallets.common.vo.ClearAccountVO;
import com.asianwallets.common.vo.FrozenMarginInfoVO;
import com.asianwallets.common.vo.TmMerChTvAcctBalanceVO;
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
    public List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return accountService.exportAccountList(accountSearchDTO);
    }

    @ApiOperation(value = "查询清算户余额流水详情")
    @PostMapping("/pageClearLogs")
    public BaseResponse pageClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO) {
        return ResultUtil.success(accountService.pageClearLogs(clearSearchDTO));
    }

    @ApiOperation(value = "导出清算户余额流水详情")
    @PostMapping("/exportClearLogs")
    public List<ClearAccountVO> exportClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO) {
        return accountService.exportClearLogs(clearSearchDTO);
    }

    @ApiOperation(value = "查询结算户余额流水详情")
    @PostMapping("/pageSettleLogs")
    public BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return ResultUtil.success(accountService.pageSettleLogs(accountSearchDTO));
    }

    @ApiOperation(value = "导出结算户余额流水详情")
    @PostMapping("/exportSettleLogs")
    public List<TmMerChTvAcctBalanceVO> exportSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO) {
        return accountService.exportSettleLogs(accountSearchDTO);
    }

    @ApiOperation(value = "查询冻结余额流水详情")
    @PostMapping("/pageFrozenLogs")
    public BaseResponse pageFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO frozenMarginInfoDTO) {
        return ResultUtil.success(accountService.pageFrozenLogs(frozenMarginInfoDTO));
    }

    @ApiOperation(value = "导出冻结余额流水详情")
    @PostMapping("/exportFrozenLogs")
    public List<FrozenMarginInfoVO> exportFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO accountSearchDTO) {
        return accountService.exportFrozenLogs(accountSearchDTO);
    }

    @ApiOperation(value = "分页查询商户余额")
    @PostMapping("/pageFindMerchantBalance")
    public BaseResponse pageMerchantBalance(@RequestBody @ApiParam OrdersDTO ordersDTO) {
        return ResultUtil.success(accountService.pageFindMerchantBalance(ordersDTO));
    }
}
