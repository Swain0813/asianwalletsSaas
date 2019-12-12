package com.asianwallets.channels.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ChannelsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelsOrderMapper extends BaseMapper<ChannelsOrder> {

    @Update("update channels_order set trade_status = #{status},channel_number = #{channelNumber},update_time= NOW() where id = #{orderId} and trade_status = 1")
    int updateStatusById(@Param("orderId") String orderId, @Param("channelNumber") String channelNumber, @Param("status") String status);

    @Select("select count(1) from channels_order where id = #{orderId}")
    int selectCountById(@Param("orderId") String orderId);
}