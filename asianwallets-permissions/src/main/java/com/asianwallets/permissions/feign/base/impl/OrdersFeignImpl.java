package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.ArtificialDTO;
import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.dto.OrdersRefundDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.vo.ExportOrdersVO;
import com.asianwallets.common.vo.OrdersRefundVO;
import com.asianwallets.permissions.feign.base.OrdersFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdersFeignImpl implements OrdersFeign {

    @Override
    public BaseResponse pageFindOrders(OrdersDTO ordersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getOrdersDetail(String id) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse pageFindOrdersRefund(OrdersRefundDTO ordersRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse getOrdersRefundDetail(String refundId) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<OrdersRefundVO> exportOrdersRefund(OrdersRefundDTO ordersRefundDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public List<ExportOrdersVO> exportOrders(OrdersDTO ordersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }

    @Override
    public BaseResponse updateOrderStatus(ArtificialDTO artificialDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
