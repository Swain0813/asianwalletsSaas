package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.TradeCheckAccountDTO;
import com.asianwallets.common.entity.TradeCheckAccountDetail;
import com.asianwallets.common.vo.TradeAccountDetailVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeCheckAccountDetailMapper extends BaseMapper<TradeCheckAccountDetail> {

    /**
     * 分页查询交易对账详细表信息
     *
     * @param tradeCheckAccountDTO 查询DTO
     * @return 详情表信息集合
     */
    List<TradeCheckAccountDetail> pageFindTradeCheckAccountDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

    /**
     * 导出交易对账详细表信息
     *
     * @param tradeCheckAccountDTO
     * @return
     */
    List<TradeAccountDetailVO> exportTradeCheckAccountDetail(TradeCheckAccountDTO tradeCheckAccountDTO);

}