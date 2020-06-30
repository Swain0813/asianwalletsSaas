package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.PreOrders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * 预授权订单表
 */
@Repository
public interface PreOrdersMapper extends BaseMapper<PreOrders> {

    /**
     * 根据商户订单号获取预授权订单信息
     *
     * @param merchantOrderId
     * @return
     */
    PreOrders selectMerchantOrderId(String merchantOrderId);

    /**
     * 根据商户订单号更新预授权成功的订单
     *
     * @param merchantOrderId
     * @param completeAmount
     * @param modifier
     * @param status
     * @return
     */
    @Update("update pre_orders set order_status = #{status},complete_amount=#{completeAmount},modifier=#{modifier},update_time= NOW() where merchant_order_id = #{merchantOrderId} and order_status = 1")
    int updatePreStatusByMerchantOrderId(@Param("merchantOrderId") String merchantOrderId, @Param("completeAmount") BigDecimal completeAmount,
                                         @Param("modifier") String modifier, @Param("status") Byte status);

    /**
     * 更新预授权状态 初始状态
     *
     * @param status
     * @return
     */
    @Update("update pre_orders set order_status = #{status},update_time= NOW(),channel_number =#{channelNumber},remark1 =#{remark} where id = #{id} and order_status = 0")
    int updatePreStatusById0(@Param("id") String id,@Param("channelNumber") String channelNumber, @Param("status") Byte status,@Param("remark1") String remark);
    /**
     * 更新预授权状态 预授权成功
     * @param status
     * @return
     */
    @Update("update pre_orders set order_status = #{status},update_time= NOW(),channel_number =#{channelNumber},remark =#{remark} where id = #{id} and order_status = 1")
    int updatePreStatusById1(@Param("id") String id,@Param("channelNumber") String channelNumber, @Param("status") Byte status,@Param("remark1") String remark);

}
