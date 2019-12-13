package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AuditorProvider auditorProvider;

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

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    @Override
    public OrdersDetailVO getOrdersDetail(String id) {
        return ordersMapper.selectOrdersDetailById(id, auditorProvider.getLanguage());
    }
}
