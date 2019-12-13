package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 分页查询订单信息
     *
     * @param ordersAllDTO 订单输入实体
     * @return 订单集合
     */
    @Override
    public PageInfo<Orders> pageFindOrders(OrdersDTO ordersAllDTO) {
        return new PageInfo<>(ordersMapper.pageFindOrders(ordersAllDTO));
    }
}
