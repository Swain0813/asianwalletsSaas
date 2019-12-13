package com.asianwallets.permissions.feign.base.impl;

import com.asianwallets.common.dto.OrdersDTO;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.permissions.feign.base.OrdersFeign;
import org.springframework.stereotype.Component;

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
    public BaseResponse pageFindOrdersRefund(OrdersDTO ordersDTO) {
        throw new BusinessException(EResultEnum.ERROR.getCode());
    }
}
