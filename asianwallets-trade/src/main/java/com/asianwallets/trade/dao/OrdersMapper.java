package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.Orders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;


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

    /**
     * 根据AD3的查询订单信息更新亚洲钱包的订单信息状态
     *
     * @return
     */
    @Update("update orders set trade_status =#{status},channel_number=#{channelNumber},channel_callback_time=#{channelCallbackTime},update_time=NOW() where id = #{id} and trade_status=2")
    int updateOrderByAd3Query(@Param("id") String id, @Param("status") Byte status, @Param("channelNumber") String channelNumber, @Param("channelCallbackTime") Date channelCallbackTime);
}
