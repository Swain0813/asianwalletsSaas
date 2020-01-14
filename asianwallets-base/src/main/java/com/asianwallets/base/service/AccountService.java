package com.asianwallets.base.service;
import com.asianwallets.common.base.BaseService;
import com.asianwallets.common.dto.*;
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
    List<AccountListVO> exportAccountList(AccountSearchExportDTO accountSearchDTO);

    /**
     * 查询清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    PageInfo<TmMerChTvAcctBalance> pageClearLogs(AccountSearchDTO clearSearchDTO);

    /**
     * 导出清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportClearLogs(AccountSearchExportDTO clearSearchDTO);

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
    List<TmMerChTvAcctBalanceVO> exportSettleLogs(AccountSearchExportDTO accountSearchDTO);

    /**
     * 查询冻结余额流水详情
     * @param frozenMarginInfoDTO
     * @return
     */
    PageInfo<TmMerChTvAcctBalance> pageFrozenLogs(AccountSearchDTO frozenMarginInfoDTO);

    /**
     * 导出冻结余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportFrozenLogs(AccountSearchExportDTO accountSearchDTO);

    /**
     * 商户余额查询
     * @param ordersDTO 订单输入DTO
     * @return
     */
    PageInfo<MerchantBalanceVO> pageFindMerchantBalance(OrdersDTO ordersDTO);

    /**
     * 导出商户余额
     * @param ordersDTO 订单输入DTO
     * @return
     */
    List<MerchantBalanceVO> exportMerchantBalance(OrdersDTO ordersDTO);

    /**
     * 提款设置
     * @param accountSettleDTO
     * @return
     */
    int updateAccountSettle(AccountSettleDTO accountSettleDTO);
}
