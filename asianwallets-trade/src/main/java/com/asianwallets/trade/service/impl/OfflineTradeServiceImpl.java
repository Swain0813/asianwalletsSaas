package com.asianwallets.trade.service.impl;

import com.asianwallets.common.entity.Orders;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.response.BaseResponse;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.DateToolUtils;
import com.asianwallets.trade.dao.OrdersMapper;
import com.asianwallets.trade.dto.CsbDynamicScanDTO;
import com.asianwallets.trade.service.OfflineTradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class OfflineTradeServiceImpl implements OfflineTradeService {

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 线下同机构CSB动态扫码
     *
     * @param csbDynamicScanDTO 线下同机构CSB动态扫码输入实体
     * @return BaseResponse
     */
    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = BusinessException.class)
    public BaseResponse csbDynamicScan(CsbDynamicScanDTO csbDynamicScanDTO) {
        Orders orders = new Orders();
        orders.setMerchantId(csbDynamicScanDTO.getMerchantId());
        orders.setMerchantOrderId(csbDynamicScanDTO.getOrderNo());
        orders.setOrderCurrency(csbDynamicScanDTO.getOrderCurrency());
        orders.setOrderAmount(csbDynamicScanDTO.getOrderAmount());
        orders.setMerchantOrderTime(DateToolUtils.getReqDateG(csbDynamicScanDTO.getOrderTime()));
        orders.setProductCode(csbDynamicScanDTO.getProductCode());
        orders.setImei(csbDynamicScanDTO.getImei());
        orders.setOperatorId(csbDynamicScanDTO.getOperatorId());
        orders.setCreateTime(new Date());
        orders.setCreator(csbDynamicScanDTO.getMerchantId());
        ordersMapper.insert(orders);
//        throw new BusinessException(EResultEnum.DATA_IS_NOT_EXIST.getCode());
//        int i = 1 / 0;
        return null;
    }
}
