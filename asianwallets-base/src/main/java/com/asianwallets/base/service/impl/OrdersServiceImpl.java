package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @param ordersDTO 订单输入实体
     * @return 订单集合
     */
    @Override
    public PageInfo<Orders> pageFindOrders(OrdersDTO ordersDTO) {
        return new PageInfo<>(ordersMapper.pageFindOrders(ordersDTO));
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

    /**
     * 订单导出
     *
     * @param ordersDTO 订单实体
     * @return 订单集合
     */
    @Override
    public List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO) {
        return ordersMapper.exportOrders(ordersDTO);
    }
}
