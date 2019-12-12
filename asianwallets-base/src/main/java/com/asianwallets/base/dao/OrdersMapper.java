package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Orders;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 根据设备编号查询订单
     *
     * @param imei 设备号
     * @return 订单
     */
    Orders selectByImei(String imei);
}