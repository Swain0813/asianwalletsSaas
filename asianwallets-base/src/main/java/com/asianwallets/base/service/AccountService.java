package com.asianwallets.base.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.dto.FrozenMarginInfoDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.vo.*;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 账户表 服务类
 */
public interface AccountService extends BaseService<Account> {
    /**
     *分页查询账户信息
     * @param accountSearchDTO
     * @return
     */
    PageInfo<AccountListVO> pageFindAccount(AccountSearchDTO accountSearchDTO);

    /**
     * 导出账户信息
     * @param accountSearchDTO
     * @return
     */
    List<AccountListVO> exportAccountList(AccountSearchDTO accountSearchDTO);

    /**
     * 查询清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    PageInfo<ClearAccountVO> pageClearLogs(ClearSearchDTO clearSearchDTO);

    /**
     * 导出清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<ClearAccountVO> exportClearLogs(ClearSearchDTO clearSearchDTO);

    /**
     * 查询结算户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    PageInfo<TmMerChTvAcctBalance> pageSettleLogs(AccountSearchDTO accountSearchDTO);

    /**
     * 导出结算户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportSettleLogs(AccountSearchDTO accountSearchDTO);

    /**
     * 查询冻结余额流水详情
     * @param frozenMarginInfoDTO
     * @return
     */
    PageInfo<FrozenMarginInfoVO> pageFrozenLogs(FrozenMarginInfoDTO frozenMarginInfoDTO);

    /**
     * 导出冻结余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<FrozenMarginInfoVO> exportFrozenLogs(FrozenMarginInfoDTO accountSearchDTO);

    /**
     * 商户余额查询
     * @param ordersDTO 订单输入DTO
     * @return
     */
    PageInfo<MerchantBalanceVO> pageFindMerchantBalance(OrdersDTO ordersDTO);
}
