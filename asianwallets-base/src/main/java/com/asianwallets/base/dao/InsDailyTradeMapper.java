package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.InsDailyTrade;
import com.asianwallets.common.vo.InsDailyTradeVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsDailyTradeMapper extends BaseMapper<InsDailyTrade> {

    /**
     * 分页查询机构日交易汇总表
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    List<InsDailyTrade> pageFindInsDailyTrade(OrdersDTO ordersDTO);

    /**
     * 导出机构日交易汇总表
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    List<InsDailyTradeVO> exportInsDailyTrade(OrdersDTO ordersDTO);
}