package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.PreOrders;
import org.springframework.stereotype.Repository;

/**
 * 预授权订单表
 */
@Repository
public interface PreOrdersMapper  extends BaseMapper<PreOrders> {

    /**
     * 根据商户订单号获取预授权订单信息
     * @param merchantOrderId
     * @return
     */
    PreOrders selectMerchantOrderId(String merchantOrderId);

}