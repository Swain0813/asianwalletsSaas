package com.asianwallets.base.service.impl;

import com.asianwallets.base.dao.OrderRefundMapper;
import com.asianwallets.base.service.OrdersRefundService;
import com.asianwallets.common.config.AuditorProvider;
import com.asianwallets.common.constant.TradeConstant;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.vo.OrdersRefundDetailVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdersRefundServiceImpl implements OrdersRefundService {

    @Autowired
    private AuditorProvider auditorProvider;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

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
        OrdersRefundDetailVO ordersRefundDetailVO = orderRefundMapper.selectOrdersRefundDetailById(refundId, auditorProvider.getLanguage());
        if (TradeConstant.EN_US.equals(auditorProvider.getLanguage())) {
            if (TradeConstant.FEE_TYPE_RATE.equals(ordersRefundDetailVO.getRefundRateType())) {
                ordersRefundDetailVO.setRefundRateType("Single Rate");
            } else {
                ordersRefundDetailVO.setRefundRateType("Single Quota");
            }
        } else {
            if (TradeConstant.FEE_TYPE_RATE.equals(ordersRefundDetailVO.getRefundRateType())) {
                ordersRefundDetailVO.setRefundRateType("单笔费率");
            } else {
                ordersRefundDetailVO.setRefundRateType("单笔定额");
            }
        }
        return ordersRefundDetailVO;
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
          //退款状态 1:退款中 2:退款成功 3:退款失败 4:系统创建失败
            if (ordersRefundVO.getRefundStatus() == 1) {
                ordersRefundVO.setRefundStatusStr("退款中");
            } else if (ordersRefundVO.getRefundStatus() == 2) {
                ordersRefundVO.setRefundStatusStr("退款成功");
            } else if (ordersRefundVO.getRefundStatus() == 3) {
                ordersRefundVO.setRefundStatusStr("退款失败");
            } else {
                ordersRefundVO.setRefundStatusStr("系统创建失败");
            }
            //退款类型 - 1：全额退款 2：部分退款
            if(ordersRefundVO.getRefundType()==1){
                ordersRefundVO.setRefundTypeStr("全额退款");
            }else {
                ordersRefundVO.setRefundTypeStr("部分退款");
            }
        }
        return ordersRefundVOS;
    }
}
