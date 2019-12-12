package com.asianwallets.base.service;


import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.github.pagehelper.PageInfo;

public interface OrdersService {

    /**
     * 分页查询订单信息
     *
     * @param ordersAllDTO 订单输入实体
     * @return 订单集合
     */
    PageInfo<Orders> pageFindOrders(OrdersDTO ordersAllDTO);
}
