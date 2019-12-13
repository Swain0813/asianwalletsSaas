package com.asianwallets.base.service;


import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.github.pagehelper.PageInfo;

public interface OrdersService {

    /**
     * 分页查询订单信息
     *
     * @param ordersDTO 订单输入实体
     * @return 订单集合
     */
    PageInfo<Orders> pageFindOrders(OrdersDTO ordersDTO);

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    OrdersDetailVO getOrdersDetail(String id);
}
