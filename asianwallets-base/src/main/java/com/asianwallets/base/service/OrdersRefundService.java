package com.asianwallets.base.service;

import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 退款
 */
public interface OrdersRefundService {
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

    /**
     * 退款单导出
     *
     * @param ordersRefundDTO
     * @return
     */
    List<OrdersRefundVO> exportOrdersRefund(OrdersRefundDTO ordersRefundDTO);
}
