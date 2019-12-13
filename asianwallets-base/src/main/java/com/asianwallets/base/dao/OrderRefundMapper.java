package com.asianwallets.base.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.OrderRefund;
import org.springframework.stereotype.Repository;

/**
 * 退款
 */
@Repository
public interface OrderRefundMapper extends BaseMapper<OrderRefund> {
    int deleteByPrimaryKey(String id);

    int insert(OrderRefund record);

    int insertSelective(OrderRefund record);

    OrderRefund selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderRefund record);

    int updateByPrimaryKey(OrderRefund record);
}