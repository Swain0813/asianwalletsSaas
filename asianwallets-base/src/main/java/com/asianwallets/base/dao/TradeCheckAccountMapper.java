package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.entity.TradeCheckAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeCheckAccountMapper extends BaseMapper<TradeCheckAccount> {

    /**
     * 分页查询交易对账总表信息
     *
     * @param tradeCheckAccountDTO 查询DTO
     * @return 总表信息集合
     */
    List<TradeCheckAccount> pageFindTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * 导出交易对账总表信息
     *
     * @param tradeCheckAccountDTO
     * @return
     */
    List<TradeCheckAccount> exportTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO);
}