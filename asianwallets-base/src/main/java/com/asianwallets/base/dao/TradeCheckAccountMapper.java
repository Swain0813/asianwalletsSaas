package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.entity.TradeCheckAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeCheckAccountMapper extends BaseMapper<TradeCheckAccount> {


    List<TradeCheckAccount> pageFindTradeCheckAccount(TradeCheckAccountDTO tradeCheckAccountDTO);



}