package com.asianwallets.permissions.feign.base;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.dto.FrozenMarginInfoDTO;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.vo.AccountListVO;
import com.asianwallets.common.vo.ClearAccountVO;
import com.asianwallets.common.vo.FrozenMarginInfoVO;
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
    List<AccountListVO> exportAccountList(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "查询清算户余额流水详情")
    @PostMapping("/account/pageClearLogs")
    BaseResponse pageClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO);

    @ApiOperation(value = "导出清算户余额流水详情")
    @PostMapping("/exportClearLogs")
    List<ClearAccountVO> exportClearLogs(@RequestBody @ApiParam ClearSearchDTO clearSearchDTO);

    @ApiOperation(value = "查询结算户余额流水详情")
    @PostMapping("/pageSettleLogs")
    BaseResponse pageSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "导出结算户余额流水详情")
    @PostMapping("/exportSettleLogs")
    List<TmMerChTvAcctBalance> exportSettleLogs(@RequestBody @ApiParam AccountSearchDTO accountSearchDTO);

    @ApiOperation(value = "查询冻结余额流水详情")
    @PostMapping("/pageFrozenLogs")
    BaseResponse pageFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO frozenMarginInfoDTO);

    @ApiOperation(value = "导出冻结余额流水详情")
    @PostMapping("/exportFrozenLogs")
    List<FrozenMarginInfoVO> exportFrozenLogs(@RequestBody @ApiParam FrozenMarginInfoDTO accountSearchDTO);
}
