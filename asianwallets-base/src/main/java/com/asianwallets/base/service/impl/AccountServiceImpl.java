package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.AccountMapper;
import com.asianwallets.base.dao.ReconciliationMapper;
import com.asianwallets.base.dao.TmMerChTvAcctBalanceMapper;
import com.asianwallets.base.service.AccountService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.AccountSearchDTO;
import com.asianwallets.common.dto.ClearSearchDTO;
import com.asianwallets.common.dto.FrozenMarginInfoDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Account;
import com.asianwallets.common.entity.TmMerChTvAcctBalance;
import com.asianwallets.common.vo.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账户表 服务实现类
 */
@Service
@Transactional
public class AccountServiceImpl extends BaseServiceImpl<Account> implements AccountService {


    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TmMerChTvAcctBalanceMapper tmMerChTvAcctBalanceMapper;

    @Autowired
    private ReconciliationMapper reconciliationMapper;

    /**
     * 分页查询账户信息
     *
     * @param accountSearchDTO
     * @return
     */
    @Override
    public PageInfo<AccountListVO> pageFindAccount(AccountSearchDTO accountSearchDTO) {
        //查询账户信息
        List<AccountListVO> accountListVOS = accountMapper.pageFindAccount(accountSearchDTO);
        return new PageInfo<AccountListVO>(accountListVOS);
    }

    /**
     * 导出账户信息
     *
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<AccountListVO> exportAccountList(AccountSearchDTO accountSearchDTO) {
        return accountMapper.pageFindAccount(accountSearchDTO);
    }

    /**
     * 查询清算户余额流水详情
     *
     * @param clearSearchDTO
     * @return
     */
    @Override
    public PageInfo<ClearAccountVO> pageClearLogs(ClearSearchDTO clearSearchDTO) {
        clearSearchDTO.setSort("balance_timestamp");//变动时间降序
        return new PageInfo(tmMerChTvAcctBalanceMapper.pageClearBalanceLogs(clearSearchDTO));
    }

    /**
     * 导出清算户余额流水详情
     *
     * @param clearSearchDTO
     * @return
     */
    @Override
    public List<ClearAccountVO> exportClearLogs(ClearSearchDTO clearSearchDTO) {
        return tmMerChTvAcctBalanceMapper.exportClearBalanceLogs(clearSearchDTO);
    }

    /**
     * 查询结算户余额流水详情
     *
     * @param accountSearchDTO
     * @return
     */
    @Override
    public PageInfo<TmMerChTvAcctBalance> pageSettleLogs(AccountSearchDTO accountSearchDTO) {
        accountSearchDTO.setSort("balance_timestamp");//变动时间降序
        //查询结算户余额流水详情
        List<TmMerChTvAcctBalance> pageAccountBalanceLogLists = tmMerChTvAcctBalanceMapper.pageAccountBalanceLogs(accountSearchDTO);
        return new PageInfo(pageAccountBalanceLogLists);
    }

    /**
     * 导出结算户余额流水详情
     *
     * @param accountSearchDTO
     * @return
     */
    @Override
    public List<TmMerChTvAcctBalanceVO> exportSettleLogs(AccountSearchDTO accountSearchDTO) {
        //查询结算户余额流水详情
        List<TmMerChTvAcctBalanceVO> oldAccountBalanceLogs = tmMerChTvAcctBalanceMapper.exportAccountBalanceLogs(accountSearchDTO);
        return oldAccountBalanceLogs;
    }

    /**
     * 查询冻结余额流水详情
     *
     * @param frozenMarginInfoDTO
     * @return
     */
    @Override
    public PageInfo<FrozenMarginInfoVO> pageFrozenLogs(FrozenMarginInfoDTO frozenMarginInfoDTO) {
        List<FrozenMarginInfoVO> frozenMarginInfoVOS = reconciliationMapper.pageFrozenLogs(frozenMarginInfoDTO);
        for (FrozenMarginInfoVO vo : frozenMarginInfoVOS) {
            if (vo.getStatus() == TradeConstant.UNFREEZE_SUCCESS) {
                vo.setAmount(vo.getAmount().negate());
            }
        }
        return new PageInfo<FrozenMarginInfoVO>(frozenMarginInfoVOS);
    }

    /**
     * 导出冻结余额流水详情
     *
     * @param frozenMarginInfoDTO
     * @return
     */
    @Override
    public List<FrozenMarginInfoVO> exportFrozenLogs(FrozenMarginInfoDTO frozenMarginInfoDTO) {
        List<FrozenMarginInfoVO> frozenMarginInfoVOS = reconciliationMapper.pageFrozenLogs(frozenMarginInfoDTO);
        for (FrozenMarginInfoVO vo : frozenMarginInfoVOS) {
            if (vo.getStatus() == TradeConstant.UNFREEZE_SUCCESS) {
                vo.setAmount(vo.getAmount().negate());
            }
        }
        return frozenMarginInfoVOS;
    }

    /**
     * 商户余额查询
     *
     * @param ordersDTO 订单输入DTO
     * @return
     */
    @Override
    public PageInfo<MerchantBalanceVO> pageFindMerchantBalance(OrdersDTO ordersDTO) {
        ordersDTO.setSort("balance_timestamp");
        return new PageInfo<>(tmMerChTvAcctBalanceMapper.pageFindMerchantBalance(ordersDTO));
    }

    /**
     * 导出商户余额
     *
     * @param ordersDTO 订单输入DTO
     * @return
     */
    @Override
    public List<MerchantBalanceVO> exportMerchantBalance(OrdersDTO ordersDTO) {
        List<MerchantBalanceVO> merchantBalanceVOList = tmMerChTvAcctBalanceMapper.exportMerchantBalance(ordersDTO);
        for (MerchantBalanceVO merchantBalanceVO : merchantBalanceVOList) {
            if ("NT".equals(merchantBalanceVO.getTradeType()) || ("ST".equals(merchantBalanceVO.getTradeType()))) {
                merchantBalanceVO.setTradeType("收单");
            } else if ("RF".equals(merchantBalanceVO.getTradeType())) {
                merchantBalanceVO.setTradeType("退款");
            } else if ("RV".equals(merchantBalanceVO.getTradeType())) {
                merchantBalanceVO.setTradeType("撤销");
            } else if ("WD".equals(merchantBalanceVO.getTradeType())) {
                merchantBalanceVO.setTradeType("提款");
            } else if ("AA".equals(merchantBalanceVO.getTradeType()) || "RA".equals(merchantBalanceVO.getTradeType())) {
                merchantBalanceVO.setTradeType("调账");
            } else if ("SP".equals(merchantBalanceVO.getTradeType())) {
                merchantBalanceVO.setTradeType("分润");
            }
        }
        return merchantBalanceVOList;
    }
}
