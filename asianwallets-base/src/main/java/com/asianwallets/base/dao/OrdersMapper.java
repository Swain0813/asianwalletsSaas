package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 根据设备编号查询订单
     *
     * @param imei 设备号
     * @return 订单
     */
    Orders selectByImei(String imei);

    /**
     * 分页查询订单信息
     *
     * @param ordersAllDTO 订单输入实体
     * @return 订单集合
     */
    List<Orders> pageFindOrders(OrdersDTO ordersAllDTO);
}