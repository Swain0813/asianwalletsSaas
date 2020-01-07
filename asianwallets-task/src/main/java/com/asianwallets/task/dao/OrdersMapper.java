package com.asianwallets.task.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.InsDailyTradeVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 机构日交易汇总表
     *
     * @param yesterday 昨日日期
     * @return
     */
    List<InsDailyTradeVO> insDailyTradeReport(String yesterday);
}