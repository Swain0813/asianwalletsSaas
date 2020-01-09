package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.AccountSearchExportDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.vo.ClearAccountVO;
import com.asianwallets.common.vo.MerchantBalanceVO;
import com.asianwallets.common.vo.TmMerChTvAcctBalanceVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TmMerChTvAcctBalanceMapper extends BaseMapper<TmMerChTvAcctBalance> {

    /**
     * 查询清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalance> pageClearBalanceLogs(AccountSearchDTO clearSearchDTO);

    /**
     * 导出清算户余额流水详情
     * @param clearSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportClearBalanceLogs(AccountSearchExportDTO clearSearchDTO);

    /**
     * 查询结算户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalance> pageAccountBalanceLogs(AccountSearchDTO accountSearchDTO);

    /**
     * 导出账户余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportAccountBalanceLogs(AccountSearchExportDTO accountSearchDTO);


    /**
     * 查询冻结流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalance> pageFrozenLogs(AccountSearchDTO accountSearchDTO);

    /**
     * 导出冻结余额流水详情
     * @param accountSearchDTO
     * @return
     */
    List<TmMerChTvAcctBalanceVO> exportFrozenLogs(AccountSearchExportDTO accountSearchDTO);

    /**
     * 商户余额查询
     *
     * @param ordersDTO 订单输入DTO
     * @return
     */
    List<MerchantBalanceVO> pageFindMerchantBalance(OrdersDTO ordersDTO);

    /**
     * 导出商户余额
     * @param ordersDTO 订单输入DTO
     * @return
     */
    List<MerchantBalanceVO> exportMerchantBalance(OrdersDTO ordersDTO);
}