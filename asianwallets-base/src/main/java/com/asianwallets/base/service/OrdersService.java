package com.asianwallets.base.service;


import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.github.pagehelper.PageInfo;

public interface OrdersService {

    /**
     * 分页查询订单信息
     *
     * @param ordersAllDTO 订单输入实体
     * @return 订单集合
     */
    PageInfo<Orders> pageFindOrders(OrdersDTO ordersAllDTO);

    /**
     * 查询订单详情信息
     *
     * @param id 订单id
     * @return 订单详情输出实体
     */
    OrdersDetailVO getOrdersDetail(String id);

    /**
     * 分页查询退款订单信息
     *
     * @param ordersRefundDTO 订单输入实体
     * @return 退款订单集合
     */
    PageInfo<OrdersRefundVO> pageFindOrdersRefund(OrdersRefundDTO ordersRefundDTO);

    /**
     * 查询退款订单详情信息
     *
     * @param refundId 退款订单ID
     * @return OrdersRefundDetailVO
     */
    OrdersRefundDetailVO getOrdersRefundDetail(String refundId);
}
