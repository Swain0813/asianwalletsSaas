package com.asianwallets.base.service;

import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.entity.TradeCheckAccount;
import com.asianwallets.common.entity.TradeCheckAccountDetail;
import com.asianwallets.common.vo.ExportTradeAccountVO;
import com.github.pagehelper.PageInfo;

public interface TradeCheckAccountService {


    /**
     * 生成昨日商户交易对账单
     */
    void tradeCheckAccount();

    /**
     * 分页查询交易对账总表信息
     *
     * @param tradeCheckAccountDTO 查询DTO
     * @return 总表信息集合
     */
    PageInfo<TradeCheckAccount> pageFindTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * 分页查询交易对账详细表信息
     *
     * @param tradeCheckAccountDTO 查询DTO
     * @return 详情表信息集合
     */
    PageInfo<TradeCheckAccountDetail> pageFindTradeCheckAccountDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * 导出商户交易对账单
     *
     * @param tradeCheckAccountDTO 查询DTO
     * @return 对账单
     */
    ExportTradeAccountVO exportTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO);
}
