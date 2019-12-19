package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Orders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Repository
public interface OrdersMapper extends BaseMapper<Orders> {

    Orders selectByMerchantOrderId(String merchantOrderId);

    /**
     * 根据订单号修改退款状态
     *
     * @param merchantOrderId
     * @param refundStatus
     * @return
     */
    @Update("update orders set refund_status = #{refundStatus},update_time= NOW() where merchant_order_id = #{merchantOrderId} and trade_status = 3")
    int updateOrderRefundStatus(@Param("merchantOrderId") String merchantOrderId, @Param("refundStatus") Byte refundStatus);

    /**
     * 根据商户订单号更新订单信息表中的撤销状态以及更新人
     *
     * @param merchantOrderId
     * @param deviceOperator
     * @param cancelStatus
     */
    @Update("update orders set cancel_status=#{cancelStatus},modifier=#{deviceOperator},update_time=NOW() where merchant_order_id = #{merchantOrderId} and trade_status in (2,3)")
    int updateOrderCancelStatus(@Param("merchantOrderId") String merchantOrderId, @Param("deviceOperator") String deviceOperator, @Param("cancelStatus") Byte cancelStatus);

}
