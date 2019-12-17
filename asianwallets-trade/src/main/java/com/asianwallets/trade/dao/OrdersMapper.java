package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Orders;
import org.springframework.stereotype.Repository;


@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    Orders selectByMerchantOrderId(String merchantOrderId);
}