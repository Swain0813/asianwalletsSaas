package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.OrderRefundMapper;
import com.asianwallets.base.dao.OrdersMapper;
import com.asianwallets.base.service.OrdersService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersDetailVO;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
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
    private OrderRefundMapper orderRefundMapper;

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
     * 分页查询退款订单信息
     *
     * @param ordersRefundDTO 订单输入实体
     * @return 退款订单集合
     */
    @Override
    public PageInfo<OrdersRefundVO> pageFindOrdersRefund(OrdersRefundDTO ordersRefundDTO) {
        return new PageInfo<>(orderRefundMapper.pageFindOrdersRefund(ordersRefundDTO));
    }

    /**
     * 查询退款订单详情信息
     *
     * @param refundId 退款订单ID
     * @return OrdersRefundDetailVO
     */
    @Override
    public OrdersRefundDetailVO getOrdersRefundDetail(String refundId) {
        return orderRefundMapper.selectOrdersRefundDetailById(refundId, auditorProvider.getLanguage());
    }

    /**
     * 退款单导出
     *
     * @param ordersRefundDTO
     * @return
     */
    @Override
    public List<OrdersRefundVO> exportOrdersRefund(OrdersRefundDTO ordersRefundDTO) {
        ordersRefundDTO.setPageSize(Integer.MAX_VALUE);
        List<OrdersRefundVO> ordersRefundVOS = orderRefundMapper.pageFindOrdersRefund(ordersRefundDTO);
        for (OrdersRefundVO ordersRefundVO : ordersRefundVOS) {
//            退款状态 1:退款中 2:退款成功 3:退款失败 4:系统创建失败
            if (ordersRefundVO.getRefundStatus() == 1) {
                ordersRefundVO.setRefundStatusStr("退款中");
            } else if (ordersRefundVO.getRefundStatus() == 2) {
                ordersRefundVO.setRefundStatusStr("退款成功");
            } else if (ordersRefundVO.getRefundStatus() == 3) {
                ordersRefundVO.setRefundStatusStr("退款失败");
            } else {
                ordersRefundVO.setRefundStatusStr("系统创建失败");
            }
        }
        return ordersRefundVOS;
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
