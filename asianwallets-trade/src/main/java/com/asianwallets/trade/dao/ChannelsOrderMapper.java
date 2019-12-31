package com.asianwallets.trade.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ChannelsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelsOrderMapper extends BaseMapper<ChannelsOrder> {

    /**
     * 根据备注查询订单
     *
     * @param remark1 备注1
     * @return 通道订单
     */
    ChannelsOrder selectByRemarks(@Param("remark1") String remark1, @Param("remark2") String remark2, @Param("remark3") String remark3);

    /**
     * 根据id和交易状态更新上报通道订单记录中的通道流水号
     *
     * @param orderId       订单ID
     * @param channelNumber 通道流水号
     * @param status        状态
     * @return
     */
    @Update("update channels_order set trade_status = #{status},channel_number = #{channelNumber},update_time= NOW() where id = #{orderId} and trade_status = 1")
    int updateStatusById(@Param("orderId") String orderId, @Param("channelNumber") String channelNumber, @Param("status") Byte status);

    /**
     * 根据id和交易状态更新上报通道订单记录中的备注
     *
     * @param remark 备注
     * @param status 状态
     * @return
     */
    @Update("update channels_order set remark = #{remark} update_time= NOW() where id = #{orderId} and trade_status = 1")
    int updateRemarkById(@Param("remark") String remark, @Param("status") Byte status);

    /**
     * 根据orderID查找订单
     *
     * @param orderId
     * @return
     */
    @Select("select count(1) from channels_order where id = #{orderId}")
    int selectCountById(@Param("orderId") String orderId);
}